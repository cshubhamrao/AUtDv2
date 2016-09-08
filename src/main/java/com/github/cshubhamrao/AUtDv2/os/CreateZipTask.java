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
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Creates a .zip of the "Path" passed.
 *
 * @author "Shubham Rao <cshubhamrao@gmail.com>"
 */
public class CreateZipTask implements Callable<Path> {

    private static final java.util.logging.Logger logger = Log.logger;

    private final File projectFolder;

    /**
     *
     * @param projectLocation path to the folder to zip.
     */
    public CreateZipTask(File projectLocation) {
        this.projectFolder = projectLocation;
    }

    @Override
    public Path call() throws IOException {
        Path folder = projectFolder.toPath();
        Path zipFile = Paths.get("", folder.getFileName() + ".zip");

        long time = System.currentTimeMillis();

        try (ZipOutputStream zipStream = new ZipOutputStream(Files.newOutputStream(
                zipFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
            Files.walkFileTree(folder, new ZipperVisitor(zipStream, folder));
        }

        long newTime = System.currentTimeMillis();
        logger.log(Level.INFO, "Took {0} ms to create zip file", (newTime - time));
        return zipFile;
    }

    protected static class ZipperVisitor extends SimpleFileVisitor<Path> {

        private final ZipOutputStream zipStream;
        private final Path rootDir;

        private ZipperVisitor(ZipOutputStream zipStream, Path rootDir) throws IOException {
            this.rootDir = rootDir;
            this.zipStream = zipStream;
            zipStream.setLevel(6);
        }

        @Override
        public FileVisitResult preVisitDirectory(Path p, BasicFileAttributes bfa) throws IOException {
            List<String> ignoredDirs = Arrays.asList(new String[]{
                ".git",
                "build",
                "dist",
                "target"
            });
            if (ignoredDirs.contains(p.getFileName().toString())) {
                return FileVisitResult.SKIP_SUBTREE;
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path p, BasicFileAttributes bfa) throws IOException {
            String relativeEntry = rootDir.getParent().relativize(p).toString();
            relativeEntry = relativeEntry.replaceAll("\\\\", "/");
            zipStream.putNextEntry(new ZipEntry(relativeEntry));
            Files.copy(p, zipStream);
            return FileVisitResult.CONTINUE;
        }
    }
}
