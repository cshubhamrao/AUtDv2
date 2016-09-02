/*
 * The MIT License
 *
 * Copyright 2016 Shubham Rao.
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
package com.github.cshubhamrao.AUtDv2.os;

import com.github.cshubhamrao.AUtDv2.util.Log;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Runs mysqldump to create DB dumps. The dumps are stored as {@code dbName.sql} in the current
 * directory. The database is assumed to exist.
 *
 * @author Shubham Rao (cshubhamrao@gmail.com)
 */
public class MySqlDumpRunner extends AppRunner {

    private static final Logger logger = Log.logger;
    private final String dbName;
    private final String password;

    /**
     *
     * @param dbName name of database to dump
     */
    public MySqlDumpRunner(String dbName) {
        this(dbName, "root");
    }

    /**
     *
     * @param dbName name of database to dump
     * @param password password to use with MySQL
     */
    public MySqlDumpRunner(String dbName, String password) {
        this.dbName = dbName;
        this.password = password;
    }

    @Override
    void setCommand() {
        CommandLine command = new CommandLine();
        switch(os) {
            case WINDOWS:
                String cmd = Paths.get(System.getenv("WINDIR"), "system32", "cmd.exe").toString();
                command.setCommandName(Paths.get(windowsLocation(), "mysqldump.exe").toString());
                command.addArguments("--user=root", "--password=" + "\"" + password + "\"");
                command.addArguments("--hex-blob");
                command.addArguments("--result-file="
                        + Paths.get("", dbName + ".sql").toAbsolutePath().toString());
                command.addArguments("\"" + dbName + "\"");
        }
        logger.log(Level.INFO, "Dumping {0}", dbName);
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
            } catch (UncheckedIOException | IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }

        location = mySqlLocs.last().resolve("bin");
        logger.log(Level.INFO, "Using {0} for MySQL", location.toString());
        return location.toString();
    }
}
