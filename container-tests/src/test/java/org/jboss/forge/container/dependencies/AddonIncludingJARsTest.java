package org.jboss.forge.container.dependencies;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.ContainerControl;
import org.jboss.forge.container.Status;
import org.jboss.forge.test.Tests;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AddonIncludingJARsTest
{
   @Deployment
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addAsLibraries(Tests.resolveDependencies("javax.enterprise:cdi-api:1.0"))
               .addBeansXML();

      return archive;
   }

   @Inject
   private ContainerControl control;

   @Test
   public void testContainerInjectionWorksWithConflictingCDIDependency()
   {
      Assert.assertNotNull(control);
      Assert.assertEquals(Status.STARTED, control.getStatus());
   }

}