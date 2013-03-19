/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.forge.container;

import java.util.Set;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.ValidIdRange;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@MessageLogger(projectCode = "FORGE")
@ValidIdRange(min = 100, max = 499)
public interface ForgeLogger extends BasicLogger
{
   final ForgeLogger CONTAINER = Logger.getMessageLogger(ForgeLogger.class, ForgeLogger.class.getPackage().getName());
   final ForgeLogger CONTAINER_IMPL = Logger.getMessageLogger(ForgeLogger.class, "org.jboss.forge.container.impl");

   @LogMessage(level = Level.INFO)
   @Message(id = 100, value = "Failed loading paths from: [%s]. Attempting folder discovery")
   void failedLoadingPaths(String path);

   @LogMessage(level = Level.WARN)
   @Message(id = 101, value = "Could not detect Forge runtime version - loading all addons, but failures may occur if versions are not compatible.")
   void couldNotDetectRuntimeVersion();

   @LogMessage(level = Level.INFO)
   @Message(id = 102, value = "Starting addon (%s)")
   void startingAddOn(AddonId addonId);

   @LogMessage(level = Level.WARN)
   @Message(id = 103, value = "Failed to shut down addon %s")
   void failedToShutDown(@Cause Throwable cause, Addon addon);

   @LogMessage(level = Level.INFO)
   @Message(id = 104, value = "(%d) addons were aborted while loading.")
   void abortedAddons(int size);

   @LogMessage(level = Level.INFO)
   @Message(id = 105, value = "Not loading addon [%s] because it references Forge API version [%s] " +
         "which may not be compatible with my current version [%s].")
   void notLoadingAddon(String name, String apiVersion, String runtimeApiVersion);

   @LogMessage(level = Level.INFO)
   @Message(id = 106, value = "Addon [%s] has [%d] missing dependencies: %s and will be not be loaded until all required"
         + " dependencies are available.")
   void addonMissingDependencies(Addon addon, int size, Set<AddonDependency> missingDependencies);

   @LogMessage(level = Level.WARN)
   @Message(id = 107, value = "Failed to start addon %s")
   void failedToStart(@Cause Throwable cause, AddonId addonId);
}
