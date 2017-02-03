/*
 * The MIT License
 *
 * Copyright 2016 Shubham Rao <cshubhamrao@gmail.com>s.
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

import java.time.LocalDate;

/**
 *
 * @author "Shubham Rao (cshubhamrao@gmail.com)"
 */
public class Classwork {

    private int rowID;
    public static final String tblName = "ClassWork";
    public static final String fieldList = "cw_no, date, topic, desc";

    private int cw_number;
    private LocalDate date;
    private Topic topic;
    private String description;

    public Classwork(int cwNo, LocalDate date, Topic topic, String desc) {
        cw_number = cwNo;
        this.date = date;
        this.topic = topic;
        description = desc;
    }

    public static String getSchema() {
        String query;
        query = "CREATE TABLE IF NOT EXISTS "
                + "ClassWork "
                + "(rowId INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "cw_no INT, "
                + "date DATE NOT NULL, "
                + "topic TEXT CHECK (topic IN('Java', 'MySQL')), "
                + "desc TEXT);";
        return query;
    }

    public int getCw_number() {
        return cw_number;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getTopic() {
        if(topic == Topic.JAVA) return "Java";
        else return "MySQL";
    }

    public String getDescription() {
        return description;
    }

    public int getRowID() {
        return rowID;
    }

    public void setRowID(int rowID) {
        this.rowID = rowID;
    }

    public enum Topic {
        JAVA,
        MYSQL
    }
}
