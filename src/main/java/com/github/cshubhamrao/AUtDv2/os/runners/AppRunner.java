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
package com.github.cshubhamrao.AUtDv2.os.runners;

import com.github.cshubhamrao.AUtDv2.os.OSLib;
import com.github.cshubhamrao.AUtDv2.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;

/**
 * An interface to allow running programs from the OS. Implementations have to set the
 * {@link CommandLine} for the application to be executed, by overriding the
 * {@link AppRunner#setCommand()} and calling {@link AppRunner#setCommand(CommandLine)} inside the
 * method.
 *
 * @see OSLib
 * @author Shubham Rao (cshubhamrao@gmail.com)
 */
public abstract class AppRunner implements Callable<Integer> {

    private static final java.util.logging.Logger logger = Log.logger;

    /**
     * Sets the {@link CommandLine} to execute.
     *
     * This function must be called by implementations inside {@link AppRunner#setCommand()} when it
     * is overridden.
     *
     * @param command command to execute
     */
    protected void setCommand(CommandLine command) {
        this.command = command;
    }

    private CommandLine command;

    /**
     * Current Architecture
     *
     * @see OSLib#getCurrentArchitecture()
     */
    public final OSLib.Architecture arch;

    /**
     * Current Operating System
     *
     * @see OSLib#getCurrentOS()
     */
    public final OSLib.OperatingSystem os;

    AppRunner() {
        this.arch = OSLib.getCurrentArchitecture();
        this.os = OSLib.getCurrentOS();
    }

    abstract void setCommand();

    CommandLine getCommand() {
        return command;
    }

    /**
     * Runs the OS specific Command. Exit code is logged.
     *
     * @throws java.io.IOException
     */
    @Override
    public Integer call() throws IOException {
        setCommand();
        ProcessBuilder pb = new ProcessBuilder(command.getFullCommand());
        int exit = -1;
        try {
            Process p = pb.start();
            logger.log(Level.INFO, "Started runnning command");
            exit = p.waitFor();
            logger.log(Level.INFO, "Exit Code: {0} ", exit);
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, "Error getting exit code", ex);
        }
        return exit;
    }

    /**
     * Class representing a Command along with its arguments.
     *
     * Encapsulates an OS dependent Command, with executable's name and its arguments.
     *
     * @author Shubham Rao (cshubhamrao@gmail.com)
     */
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

        /**
         * Gets the name of executable.
         *
         * @return name of executable
         */
        public String getCommandName() {
            return commandName;
        }

        /**
         * Sets the name of executable.
         *
         * @param commandName name of executable
         */
        public void setCommandName(String commandName) {
            this.commandName = commandName;
            updateFullCommand();
        }

        /**
         * Gets the arguments
         *
         * @return arguments
         */
        public List<String> getArguments() {
            return arguments;
        }

        /**
         * Adds arguments to the command.
         *
         * @param args arguments
         */
        public final void addArguments(String... args) {
            arguments.addAll(Arrays.asList(args));
            updateFullCommand();
        }

        /**
         * Replaces arguments of the command.
         *
         * @param arguments list containing arguments
         */
        public void setArguments(ArrayList<String> arguments) {
            this.arguments = arguments;
            updateFullCommand();
        }

        /**
         * Returns the command name along with its arguments.
         *
         * First element is the command name, remaining elements are its arguments.
         *
         * @return command with arguments included.
         */
        public List<String> getFullCommand() {
            logger.log(Level.INFO, "Command: {0}", this.toString());
            return fullCommand;
        }

        /**
         * Sets the command name along with its arguments.
         *
         * First element must be the command name, remaining elements, its arguments.
         *
         * @param fullCommand command along with arguments
         */
        public void setFullCommand(ArrayList<String> fullCommand) {
            this.fullCommand = fullCommand;
        }

        /**
         * Returns command and its arguments as a String.
         *
         * @return command and its arguments as String
         */
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
