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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Shubham Rao <cshubhamrao@gmail.com>
 */
abstract class AppRunner {

    String location;
    OSLib.OperatingSystem os;
    OSLib.Architecture arch;
    List<String> args = new ArrayList<>();

    AppRunner(String... args) {
        this.os = OSLib.getCurrentOS();
        this.args.addAll(Arrays.asList(args));
        this.arch = OSLib.getCurrentArchitecture();
    }

    AppRunner() {
        this((String) null);
    }

    abstract String findLocation();

    void run() {
        validateLocation();
        if (!location.isEmpty()) {
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(location);
            try {
                Process p = pb.start();
                System.out.println("Started running...");
            }
            catch (IOException ex) {
                System.out.println("Error running");
            }
        }
        else {
            System.out.println("Unable to find valid executable to run.");
        }
    }

    private void validateLocation() {
        String loc = this.findLocation();
        File f = new File(loc);
        boolean exists = f.exists() && f.isFile(),
                canExec = f.canExecute(),
                isExec = checkFormat(f);
        if (exists && canExec && isExec) {
            this.location = loc;
        }
        else {
            location = "";
        }
    }

    private boolean checkFormat(File file) {
        //TODO: Implement
        return true;
    }
}
