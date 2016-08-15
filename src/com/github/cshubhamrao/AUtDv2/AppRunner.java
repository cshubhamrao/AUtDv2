/*
 * The MIT License
 *
 * Copyright 2016 Shubham Rao <cshubhamrao@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.cshubhamrao.AUtDv2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shubham Rao <cshubhamrao@gmail.com>
 */
abstract class AppRunner {

    private static final java.util.logging.Logger logger = Log.logger;

    protected void setCommand(CommandLine command) {
        this.command = command;
    }

    private CommandLine command;

    public final OSLib.Architecture arch;
    public final OSLib.OperatingSystem os;

    AppRunner() {
        this.arch = OSLib.getCurrentArchitecture();
        this.os = OSLib.getCurrentOS();
    }

    abstract void setCommand();

    CommandLine getCommand() {
        return command;
    }

    public void run() {
        setCommand();
        ProcessBuilder pb = new ProcessBuilder(command.getFullCommand());
        try {
            Process p = pb.start();
            logger.log(Level.INFO, "Started runnning command");
            new Thread(() -> {
                try {
                    logger.log(Level.INFO, "Exit Code: {0} ", p.waitFor());
                }
                catch (InterruptedException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
            ).start();
        }
        catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    protected class CommandLine {

        private String commandName;
        private ArrayList<String> arguments = new ArrayList();

        private ArrayList<String> fullCommand = new ArrayList();

        CommandLine() {

        }

        CommandLine(String command, String... args) {
            this.commandName = command;
            addArguments(args);
        }

        CommandLine(String command) {
            this(command, "");
        }

        public String getCommandName() {
            return commandName;
        }

        public void setCommandName(String commandName) {
            this.commandName = commandName;
            updateFullCommand();
        }

        public List<String> getArguments() {
            return arguments;
        }

        public final void addArguments(String... args) {
            arguments.addAll(Arrays.asList(args));
            updateFullCommand();
        }

        public void setArguments(ArrayList<String> arguments) {
            this.arguments = arguments;
            updateFullCommand();
        }

        public List<String> getFullCommand() {
            logger.log(Level.INFO, "Command: {0}", this.toString());
            return fullCommand;
        }

        public void setFullCommand(ArrayList<String> fullCommand) {
            this.fullCommand = fullCommand;
        }

        @Override
        public String toString() {
            StringBuilder command = new StringBuilder(this.fullCommand.get(0));
            arguments.forEach((String arg) -> command.append(" " + arg));
            return command.toString();
        }

        private void updateFullCommand() {
            this.fullCommand.clear();
            this.fullCommand.add(commandName);
            this.fullCommand.addAll(arguments);
        }
    }
}