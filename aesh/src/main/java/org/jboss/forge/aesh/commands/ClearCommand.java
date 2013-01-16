/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh.commands;

import org.jboss.aesh.cl.CommandLineParser;
import org.jboss.aesh.console.Console;
import org.jboss.forge.ui.*;
import org.jboss.forge.ui.impl.UIInputImpl;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ClearCommand implements UICommand {

    private Console console;
    private CommandLineParser parser;

    private UIInput<String> clear;

    public ClearCommand(Console console) {
        setConsole(console);
    }

    private void setConsole(Console console) {
        this.console = console;
    }

    @Override
    public void initializeUI(UIContext context) throws Exception {
        clear = new UIInputImpl<String>("clear", String.class);
        clear.setLabel("clear");
        clear.setRequired(true);
        context.getUIBuilder().add(clear);
    }

    @Override
    public void validate(UIValidationContext context) {
    }

    @Override
    public Result execute(UIContext context) throws Exception {
        console.clear();
        return Result.success("");
    }

}