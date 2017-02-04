/*
 * The MIT License
 *
 * Copyright 2017 Shubham Rao <cshubhamrao@gmail.com>.
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
package com.github.cshubhamrao.AUtDv2.models;

/**
 *
 * @author "Shubham Rao (cshubhamrao@gmail.com)"
 */
public class NetbeansProject {

    public static final String FIELD_LIST = "name, folder, zip_file";
    public static final String TABLE_NAME = "NetBeans";

    private int id;
    private final String name;
    private final String folder;
    private final String zip_file;

    public NetbeansProject(String name, String folder, String zip_file) {
        this.name = name;
        this.folder = folder;
        this.zip_file = zip_file;
    }

    public static String getSchema() {
        String query;
        query = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, "
                + "folder TEXT NOT NULL, "
                + "zip_file TEXT);";
        return query;
    }

    public String getFolder() {
        return folder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getZip_file() {
        return zip_file;
    }
}
