package org.jboss.forge.container.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.container.ContainerControl;
import org.jboss.forge.container.Forge;
import org.jboss.forge.container.ForgeLogger;
import org.jboss.forge.container.ForgeMessages;
import org.jboss.forge.container.Status;
import org.jboss.forge.container.event.Perform;
import org.jboss.forge.container.modules.ModularURLScanner;
import org.jboss.forge.container.modules.ModularWeld;
import org.jboss.forge.container.modules.ModuleResourceLoader;
import org.jboss.forge.container.modules.ModuleScanResult;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.forge.container.util.Assert;
import org.jboss.forge.container.util.BeanManagerUtils;
import org.jboss.forge.container.util.ClassLoaders;
import org.jboss.forge.container.util.Threads;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.resources.spi.ResourceLoader;

/**
 * Loads an addon
 */
public final class AddonRunnable implements Runnable
{

   private Forge forge;
   private AddonImpl addon;
   private AddonContainerStartup container;

   private Callable<Object> shutdownCallable = new Callable<Object>()
   {
      @Override
      public Object call() throws Exception
      {
         addon.setStatus(Status.STOPPING);
         addon.setStatus(Status.STOPPED);
         return null;
      }
   };

   public AddonRunnable(Forge forge, AddonImpl addon)
   {
      this.forge = forge;
      this.addon = addon;
   }

   public void shutdown()
   {
      ForgeLogger.CONTAINER_IMPL.tracef("< Stopping container [%s]", addon.getId());
      long start = System.currentTimeMillis();
      ClassLoaders.executeIn(addon.getClassLoader(), shutdownCallable);
      ForgeLogger.CONTAINER_IMPL.tracef("<< Stopped container [%s] - %d ms                    <<", addon.getId(), (System.currentTimeMillis() - start));
   }

   @Override
   public void run()
   {
      ForgeLogger.CONTAINER_IMPL.tracef("< Starting container [%s]", addon.getId());
      long start = System.currentTimeMillis();
      container = new AddonContainerStartup();
      shutdownCallable = ClassLoaders.executeIn(addon.getClassLoader(), container);
      ForgeLogger.CONTAINER_IMPL.tracef("<< Started container [%s] - %d ms                    <<", addon.getId(), (System.currentTimeMillis() - start));
   }

   public AddonImpl getAddon()
   {
      return addon;
   }

   public class AddonContainerStartup implements Callable<Callable<Object>>
   {
      private Future<Object> operation;

      @Override
      public Callable<Object> call() throws Exception
      {
         try
         {
            addon.setStatus(Status.STARTING);
            ResourceLoader resourceLoader = new ModuleResourceLoader(addon.getModule());
            ModularURLScanner scanner = new ModularURLScanner(resourceLoader, "META-INF/beans.xml");
            ModuleScanResult scanResult = scanner.scan();

            if (scanResult.getDiscoveredResourceUrls().isEmpty())
            {
               /*
                * This is an import-only addon and does not require weld, nor provide remote services.
                */
               addon.setServiceRegistry(new NullServiceRegistry());
               addon.setStatus(Status.STARTED);

               return new Callable<Object>()
               {
                  @Override
                  public Object call() throws Exception
                  {
                     addon.setStatus(Status.STOPPING);
                     addon.setStatus(Status.STOPPED);
                     return null;
                  }
               };
            }
            else
            {
               final Weld weld = new ModularWeld(scanResult);
               WeldContainer container;
               container = weld.initialize();

               final BeanManager manager = container.getBeanManager();
               Assert.notNull(manager, ForgeMessages.MESSAGES.nullValue("BeanManager"));

               final ContainerControlImpl control = (ContainerControlImpl) BeanManagerUtils.getContextualInstance(
                        manager, ContainerControl.class);
               AddonRepositoryProducer repositoryProducer = BeanManagerUtils.getContextualInstance(manager,
                        AddonRepositoryProducer.class);
               repositoryProducer.setAddonDir(forge.getAddonDir());

               ForgeProducer forgeProducer = BeanManagerUtils.getContextualInstance(manager,
                        ForgeProducer.class);
               forgeProducer.setForge(forge);

               AddonRegistryProducer addonRegistryProducer = BeanManagerUtils.getContextualInstance(manager,
                        AddonRegistryProducer.class);
               addonRegistryProducer.setRegistry(forge.getAddonRegistry());

               ContainerServiceExtension extension = BeanManagerUtils.getContextualInstance(manager,
                        ContainerServiceExtension.class);
               ServiceRegistryProducer serviceRegistryProducer = BeanManagerUtils.getContextualInstance(manager,
                        ServiceRegistryProducer.class);
               serviceRegistryProducer.setServiceRegistry(new ServiceRegistryImpl(addon, manager, extension));

               Assert.notNull(control, ForgeMessages.MESSAGES.nullValue("Container control"));

               ServiceRegistry registry = BeanManagerUtils.getContextualInstance(manager, ServiceRegistry.class);
               Assert.notNull(registry, ForgeMessages.MESSAGES.nullValue("Service registry"));
               addon.setServiceRegistry(registry);

               ((AddonRegistryImpl) forge.getAddonRegistry()).clearWaiting(addon);

               ForgeLogger.CONTAINER_IMPL.debugf("Services loaded from addon [%s] -  %s", addon.getId(), registry.getServices());

               Callable<Object> listener = new Callable<Object>()
               {
                  @Override
                  public Object call() throws Exception
                  {
                     addon.setStatus(Status.STOPPING);
                     control.removeShutdownListener(this);
                     control.stop();
                     weld.shutdown();
                     operation.cancel(true);
                     addon.setStatus(Status.STOPPED);
                     return null;
                  }
               };

               control.registerShutdownListener(listener);
               control.start();

               operation = Threads.runAsync(new Callable<Object>()
               {
                  @Override
                  public Object call() throws Exception
                  {
                     manager.fireEvent(new Perform());
                     return null;
                  }
               });

               addon.setStatus(Status.STARTED);

               return listener;
            }
         }
         catch (Exception e)
         {
            addon.setStatus(Status.FAILED);
            ForgeLogger.CONTAINER_IMPL.failedToStart(e, addon.getId());
            throw e;
         }
      }
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((addon == null) ? 0 : addon.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      AddonRunnable other = (AddonRunnable) obj;
      if (addon == null)
      {
         if (other.addon != null)
            return false;
      }
      else if (!addon.equals(other.addon))
         return false;
      return true;
   }
}
