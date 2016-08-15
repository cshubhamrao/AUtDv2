/*
 * The MIT License
 *
 * Copyright 2016 "Shubham Rao <cshubhamrao@gmail.com>".
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
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.stream.Stream;

/**
 *
 * @author "Shubham Rao <cshubhamrao@gmail.com>"
 */
public class MySqlRunner extends AppRunner {

    private static final java.util.logging.Logger logger = Log.logger;

    @Override
    void setCommand() {
        CommandLine command = new CommandLine();
        switch (os) {
            case WINDOWS:
                /*
                 Basically we run mysql through a cmd.exe(1) which "starts" another cmd.exe(2) with appropriate title
                 and command line args for mysql.exe.
                 cmd.exe(1) exits immediately after running cmd.exe(2), making mysql command prompt run independent of
                 the program.

                 MODIFY AT YOUR OWN RISK
                 Took hours to figure out and "understand" how to make this work as expected.
                 */
                String cmd = Paths.get(System.getenv("WINDIR"), "system32", "cmd.exe").toString();
                command.setCommandName(cmd);

                // Makes cmd.exe(1) accept a "command" and exit immediately after execution.
                command.addArguments("/C");

                // "command" for cmd.exe(1) is "start"
                command.addArguments("start", "\"MySQL Command Line\"");
                command.addArguments("/D", windowsLocation());

                // Runs cmd.exe(2), which runs mysql.exe with proper arguments
                command.addArguments("cmd /C", "mysql.exe", "-uroot", "-p");
                break;
            case MAC:
                logger.log(Level.WARNING, "Mac OS is untesed. Things may not work.");
            case LINUX:
                command.setCommandName("mysql");
                break;
            case UNKNOWN:
                logger.log(Level.SEVERE, "UNIMPLEMENTED");
        }
        setCommand(command);
    }

    private String windowsLocation() {
        Path location;
        List<Path> progDirs = OSLib.getProgramDirs();
        SortedSet<Path> mySqlLocs = new TreeSet();
        for (Path dir : progDirs) {
            try (Stream<Path> subDirs = Files.walk(dir, 2)) {
                subDirs.forEach((Path p) -> {
                    if (p.toString().contains("MySQL Server")) {
                        mySqlLocs.add(p);
                        logger.log(Level.INFO, "Added {0} to mySqlLocs", p.toString());
                    }
                });
            }
            catch (UncheckedIOException | IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }

        location = mySqlLocs.last().resolve("bin");
        logger.log(Level.INFO, "Using {0} for MySQL", location.toString());
        return location.toString();
    }
}
