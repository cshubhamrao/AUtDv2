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
package com.github.cshubhamrao.AUtDv2.os;

import com.github.cshubhamrao.AUtDv2.util.Log;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;

/**
 *
 * @author Shubham Rao <cshubhamrao@gmail.com>
 */
public class NetBeansRunner extends AppRunner {

    private static final java.util.logging.Logger logger = Log.logger;

    public NetBeansRunner() {
        super();
    }

    @Override
    void setCommand() {
        String location = "";
        switch (os) {
            case WINDOWS:
                location = windowsLocation();
                break;
            case MAC:
            case LINUX:
            case UNKNOWN:
                logger.log(Level.SEVERE, "UNIMPLEMENTED");

        }
        this.setCommand(new CommandLine(location));

    }

    private String windowsLocation() {
        Path location;
        SortedSet<Path> nbLocs = new TreeSet();
        List<Path> progDirs = OSLib.getProgramDirs();

        for (Path dirs : progDirs) {
            try (DirectoryStream<Path> subDirs = Files.newDirectoryStream(dirs, "NetBeans*")) {
                subDirs.forEach((pat) -> {
                    nbLocs.add(pat);
                    logger.log(Level.INFO, "Added {0} to nbLocs", pat.toString());
                });
            }
            catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
        location = nbLocs.last().resolve("bin");
        switch (arch) {
            case AMD64:
                return location.resolve("netbeans64.exe").toString();
            case UNKNOWN:
            case i386:
                return location.resolve("netbeans.exe").toString();
        }
        
        logger.log(Level.INFO, "Using {0} as NetBeans path", location);
        return location.toString();
    }
}
