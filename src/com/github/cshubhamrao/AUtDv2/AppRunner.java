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
        System.out.println("DEBUG: CommandLine: " + command);
        try {
            Process p = pb.start();
            System.out.println("INFO: Started Running");
            new Thread(() -> {
                try {
                    System.out.print("INFO: Exit Code: ");
                    System.out.println(p.waitFor());
                }
                catch (InterruptedException ex) {
                    Logger.getLogger(AppRunner.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            ).start();
        }
        catch (IOException ex) {
            Logger.getLogger(AppRunner.class.getName()).log(Level.SEVERE, null, ex);
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
            System.out.println("DEBUG: " + fullCommand);
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