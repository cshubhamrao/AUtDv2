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

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Little utility class for OS specific tasks
 *
 * @author Shubham Rao (cshubhamrao@gmail.com)
 */
public class OSLib {

    private static final java.util.logging.Logger logger = Log.logger;

    /**
     * Enumeration representing Operating Systems.
     */
    public enum OperatingSystem {

        /**
         * The Windows family of Operating System
         */
        WINDOWS,
        /**
         * Linux (only Linux)
         */
        LINUX,
        /**
         * Mac OS and Mac OS X
         */
        MAC,
        /**
         * Unknown OS
         */
        UNKNOWN
    }

    /**
     * Enumeration representing Architecture.
     */
    public enum Architecture {

        /**
         * 32-bit architecture
         */
        i386,
        /**
         * 64-bit architecture
         */
        AMD64,
        /**
         * Unknown architecture
         */
        UNKNOWN
    }

    /**
     * Gets the current OS where JVM is executing.
     *
     * @return current OS
     */
    public static OperatingSystem getCurrentOS() {
        String os = System.getProperty("os.name");
        if (os.startsWith("Linux")) return OperatingSystem.LINUX;
        else if (os.startsWith("Windows")) return OperatingSystem.WINDOWS;
        else if (os.startsWith("Mac")) return OperatingSystem.WINDOWS;
        else return OperatingSystem.UNKNOWN;
        }

    /**
     * Gets the current Architecture where JVM is executing.
     *
     * @return current architecture
     */
    public static Architecture getCurrentArchitecture() {
        String arch = System.getProperty("os.arch");
        if (arch.startsWith("amd64")) return Architecture.AMD64;
        /*
        ¯\_(ツ)_/¯
        STORY TIME: with a brave warrior tester [REDACTED], a troubled developer, a [baby-like]app
        and a [sluggish]PC
        Once upon a time (24/08/2016, 22:54 + ~00:20:00), a troubled developer (no points for
        guessing who) was in conversation about the pre-alpha build of his app. As with everything,
        things did not work(tm). Our brave warrior, [REDACTED] battled through strange errors (with
        no error info) about JVM creation; then updating JRE version; then inconsistent behaviour of
        GUI vs CMD... all on the (mobile)phone. Finally the GUI window opens, our warrior tester,
        [REDACTED] clicks "Run NetBeans", lo and behold, *nothing* happens. [REDACTED] tries again,
        and the app... does nothing. "What went wrong?" That is the answer to the question.
        "Check the Logs"... The logs are searched, the logs are scoured and our warrior finds...
        *NOTHING*, literally. The logs have no mention of anything, let alone mention of *something*
        going wrong.
        So what went wrong?
        Our warrior tester used 32-bit Windows, a platform our baby app is not accustomed to.
        Guardian Java does little to intim[id]ate the app. Wrong architecture detection, thanks to
        Java and app thinks the architecture is UNKNOWN. Java please be consistent. Return my 12
        hours...
        */

        //      JAVA, Y U MAKE ME DO DIS?   ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
        else if (arch.startsWith("i386") || arch.contains("86")) return Architecture.i386;
        return Architecture.UNKNOWN;
    }

    /**
     * Returns directories where installed programs *can* be found.
     *
     * Current support is only for Windows, where it returns the "Program Files" directory.
     *
     * @return List of paths where programs can be installed.
     */
    public static List<Path> getProgramDirs() {
        ArrayList<Path> dirs = new ArrayList(2);
        if (getCurrentOS() == OperatingSystem.WINDOWS) {
            switch (getCurrentArchitecture()) {
                case i386:
                    dirs.add(Paths.get(System.getenv("PROGRAMFILES")));
                    break;
                case AMD64:
                    dirs.add(Paths.get(System.getenv("PROGRAMFILES")));
                    dirs.add(Paths.get(System.getenv("PROGRAMFILES(x86)")));
                    break;
                case UNKNOWN:
                    break;
            }
        } else if (getCurrentOS() == OperatingSystem.LINUX) {
            logger.log(Level.SEVERE, "Support for linux is unimplemented");
        }
        return dirs;
    }
}
