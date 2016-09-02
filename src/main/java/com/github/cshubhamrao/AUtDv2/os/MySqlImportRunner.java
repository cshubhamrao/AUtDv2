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
 * Finds and runs mysql to restore DBs from backup
 *
 * @author Shubham Rao (cshubhamrao@gmail.com)
 */
public class MySqlImportRunner extends AppRunner {

    private static final Logger logger = Log.logger;
    private final String sqlFile;
    private final String dbName;
    private final String password;

    /**
     *
     * @param sqlFile Path to .sql file containing DB Dump.
     * @param dbName Name of database to create.
     */
    public MySqlImportRunner(String sqlFile, String dbName) {
        this(sqlFile, dbName, "root");
    }

    /**
     *
     * @param sqlFile Path to .sql file containing DB Dump.
     * @param dbName Name of database to create.
     * @param password Password to use with MySQL
     */
    public MySqlImportRunner(String sqlFile, String dbName, String password) {
        this.sqlFile = sqlFile;
        this.dbName = dbName;
        this.password = password;
    }

    @Override
    void setCommand() {
        CommandLine command = new CommandLine();
        switch (os) {
            case WINDOWS:
                command.setCommandName(Paths.get(windowsLocation(), "mysql.exe").toString());
                command.addArguments("--user=root", "--password=" + "\"" + password + "\"");
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
            tmpFile = Files.createTempFile("AUtDv2_sqlFile_", null);
            tmpFile.toFile().deleteOnExit();
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
