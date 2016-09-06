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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 *
 * @author "Shubham Rao <cshubhamrao@gmail.com>"
 */
public class CreateZipTask implements Callable<Void> {

    private File projectLocation;

    public CreateZipTask(File projectLocation) {
        this.projectLocation = projectLocation;
    }

    @Override
    public Void call() throws IOException {
        Path folder = Paths.get(projectLocation.getAbsolutePath());
        Path zipFile = folder;
        Predicate<Path> ignoredFolders = (Path t) -> {
            String[] ignoreList = {
                "build",
                "dist",
                "target",
                ".git"
            };
            boolean keep = true;
            for (String dir : ignoreList) {
                if (t.endsWith(dir)) {
                    return !keep;
                }
            }
            return keep;
        };
        Stream<Path> stream;
        stream = Files.walk(folder);
        System.out.println("Count: " + stream.count());
        long time;
        LongStream.Builder str = LongStream.builder();
        for (int i = 0; i < 10; i++) {
            stream = Files.walk(folder);
            time = System.nanoTime();
            List<String> entries
                    = stream.parallel().filter(ignoredFolders).filter((p) -> !Files.isDirectory(p))
                    .map(p -> folder.getParent().relativize(p).toString())
                    .collect(Collectors.toList());
            str.add(System.nanoTime() - time);
            System.gc();
//            System.out.println("Took: " + (System.nanoTime() - time) + " ns");
        }
        System.out.println(str.build().average().getAsDouble());
        System.gc();
        time = System.nanoTime();
        stream = Files.walk(folder);
        List<String> entries2
                = stream.parallel().filter(p -> !(p.endsWith("build") | p.endsWith("dist") | p.endsWith("target")))
                .map(p -> folder.getParent().relativize(p).toString()).collect(Collectors.toList());
        System.out.println("Took: " + (System.nanoTime() - time) + " ns");
        System.out.println();

        return null;
    }

}
