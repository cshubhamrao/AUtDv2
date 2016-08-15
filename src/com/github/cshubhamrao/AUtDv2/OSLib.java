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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author Shubham Rao <cshubhamrao@gmail.com>
 */
public class OSLib {

    private static final java.util.logging.Logger logger = Log.logger;

    enum OperatingSystem {
        WINDOWS,
        LINUX,
        MAC,
        UNKNOWN
    }

    enum Architecture {
        i386,
        AMD64,
        UNKNOWN
    }

    static OperatingSystem getCurrentOS() {
        String os = System.getProperty("os.name");
        if (os.startsWith("Linux")) return OperatingSystem.LINUX;
        else if (os.startsWith("Windows")) return OperatingSystem.WINDOWS;
        else if (os.startsWith("Mac")) return OperatingSystem.WINDOWS;
        else return OperatingSystem.UNKNOWN;
    }

    static Architecture getCurrentArchitecture() {
        String arch = System.getProperty("os.arch");
        if (arch.startsWith("amd64")) return Architecture.AMD64;
        else if (arch.startsWith("i386")) return Architecture.i386;
        return Architecture.UNKNOWN;
    }
    
    static List<Path> getProgramDirs() {
        ArrayList<Path> dirs = new ArrayList(2);
        if (getCurrentOS() == OperatingSystem.WINDOWS)
        {
            switch(getCurrentArchitecture())
            {
                case i386:
                    dirs.add(Paths.get(System.getenv("PROGRAMFILES")));
                case AMD64:
                    dirs.add(Paths.get(System.getenv("PROGRAMFILES")));
                    dirs.add(Paths.get(System.getenv("PROGRAMFILES(x86)")));
                    break;
                case UNKNOWN:
                    break;
            }
        }
        else if (getCurrentOS() == OperatingSystem.LINUX)
        {
            logger.log(Level.SEVERE, "Support for linux is unimplemented");
        }
        return dirs;
    }
}
