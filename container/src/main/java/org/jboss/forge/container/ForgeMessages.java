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

import java.lang.reflect.Member;

import org.jboss.forge.container.exception.ContainerException;
import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.jboss.logging.annotations.ValidIdRange;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@MessageBundle(projectCode = "FORGE")
@ValidIdRange(min = 500, max = 999)
public interface ForgeMessages
{
   final ForgeMessages MESSAGES = Messages.getBundle(ForgeMessages.class);

   @Message(id = 500, value = "%s must not be null.")
   String nullNotAllowed(String name);

   @Message(id = 501, value = "%s was null.")
   String nullValue(String name);

   @Message(id = 502,value = "Failed to load addon [%s]")
   ContainerException failedToLoadAddon(@Cause Throwable cause, AddonId addonId);

   @Message(id = 503, value = "Cannot handle producer for non-Field and non-Method member type: %s")
   ContainerException cannotHandleProducer(Member member);
}
