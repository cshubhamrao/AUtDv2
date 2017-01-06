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
package com.github.cshubhamrao.AUtDv2.db;

import com.github.cshubhamrao.AUtDv2.models.ClassWork;
import com.github.cshubhamrao.AUtDv2.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author "Shubham Rao (cshubhamrao@gmail.com)"
 */
public class DatabaseTasks {

    private static final java.util.logging.Logger logger = Log.logger;
    Connection con;

    public void writeCW(ClassWork cw) throws SQLException {
        con = DriverManager.getConnection("jdbc:sqlite:autdv2.db");
        con.createStatement().executeUpdate(cw.getSchema());
        String query = "INSERT INTO " + ClassWork.tblName
                     + "("+ClassWork.fieldList+") "
                     + "VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setInt(1, cw.getCw_number());
        stmt.setString(2, cw.getDate().toString());
        stmt.setString(3, cw.getTopic());
        stmt.setString(4, cw.getDescription());
//        System.out.println(stmt.);
        stmt.execute();
        con.close();
    }
}
