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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
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
 *
 * @author "Shubham Rao <cshubhamrao@gmail.com>"
 */
public class MySqlImportRunner extends AppRunner {

    private static final Logger logger = Log.logger;
    private final String sqlFile;
    private final String dbName;

    public MySqlImportRunner(String sqlFile, String dbName) {
        this.sqlFile = sqlFile;
        this.dbName = dbName;
    }

    @Override
    void setCommand() {
        CommandLine command = new CommandLine();
        switch (os) {
            case WINDOWS:
                String cmd = Paths.get(System.getenv("WINDIR"), "system32", "cmd.exe").toString();
                command.setCommandName(cmd);
                command.addArguments("/C");
                command.addArguments("start", "\"Creating MySQL Dump\"");
                command.addArguments("/D", windowsLocation());
                command.addArguments("cmd /K", "mysql.exe");
                command.addArguments("--user=root", "--password", "--verbose");
                command.addArguments("-e");
                command.addArguments("\"source " + tempSqlFile() + "\"");
        }
        logger.log(Level.INFO, "Importing {0}", dbName);
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

    private String tempSqlFile() {
        String commands = "DROP DATABASE IF EXISTS " + dbName + ";\n"
                + "CREATE DATABASE " + dbName + ";\n"
                + "USE " + dbName + ";\n"
                + "source " + sqlFile;
        Path tmpFile;
        try {
            tmpFile = Files.createTempFile("AUtDv2_sqlFile", null);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error creating temporary file", ex);
            return "";
        }
        try (BufferedWriter bw = Files.newBufferedWriter(tmpFile, Charset.defaultCharset())) {
            bw.write(commands);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error writing to temporary sql file", ex);
            return "";
        }
        return tmpFile.toString();
    }
}