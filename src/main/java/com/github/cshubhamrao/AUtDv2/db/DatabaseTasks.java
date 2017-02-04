/*
 * The MIT License
 *
 * Copyright 2016-17 Shubham Rao <cshubhamrao@gmail.com>.
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

import com.github.cshubhamrao.AUtDv2.models.Classwork;
import com.github.cshubhamrao.AUtDv2.models.MySql;
import com.github.cshubhamrao.AUtDv2.models.NetbeansProject;
import com.github.cshubhamrao.AUtDv2.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.logging.Level;

/**
 *
 * @author "Shubham Rao (cshubhamrao@gmail.com)"
 */
public class DatabaseTasks {

    private static final java.util.logging.Logger logger = Log.logger;

    public static Classwork fetchCW(int cwNo) {
        String query = "SELECT * FROM " + Classwork.TABLE_NAME
                       + " WHERE cw_no = " + cwNo + " ORDER BY rowid DESC;";
        try (Connection con = getConnection();
             ResultSet rs = con.createStatement().executeQuery(query)) {
            //This is always the first row
            if (rs.next()) {
                int no = rs.getInt(2);
                LocalDate date = LocalDate.parse(rs.getString(3));
                Classwork.Topic topic
                        = Classwork.Topic.valueOf(rs.getString(4).toUpperCase());
                String desc = rs.getString(5);
                Classwork c = new Classwork(no, date, topic, desc);
                c.setRowID(rs.getInt(1));
                return c;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error Fetching CW...", ex);
        }
        return null;
    }

    public static int updateCW(int rowid, Classwork cw) {
        int newRow = 0;
        try (Connection con = getConnection()) {
            con.createStatement().executeUpdate(Classwork.getSchema());
            String query
                    = "UPDATE " + Classwork.TABLE_NAME + " SET "
                      + "cw_no = ?, "
                      + "date = ?, "
                      + "topic = ?, "
                      + "desc = ?"
                      + "WHERE rowid = " + rowid + " ;";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, cw.getCw_number());
            stmt.setString(2, cw.getDate().toString());
            stmt.setString(3, cw.getTopic());
            stmt.setString(4, cw.getDescription());
            stmt.executeUpdate();
            newRow = rowid;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error Updating CW...", ex);
        }
        return newRow;
    }

    public static int writeCW(Classwork cw) {
        int rowid = 0;
        PreparedStatement stmt = null;
        try (Connection con = getConnection()) {
            con.createStatement().executeUpdate(Classwork.getSchema());
            String query
                    = "INSERT INTO " + Classwork.TABLE_NAME
                      + "(" + Classwork.FIELD_LIST + ") "
                      + "VALUES (?, ?, ?, ?)";
            stmt = con.prepareStatement(query,
                                        Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, cw.getCw_number());
            stmt.setString(2, cw.getDate().toString());
            stmt.setString(3, cw.getTopic());
            stmt.setString(4, cw.getDescription());
            stmt.executeUpdate();
            try (ResultSet r = stmt.getGeneratedKeys()) {
                rowid = r.getInt(1);
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error getting Rowid", ex);
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error Writing CW...", ex);
        }
        return rowid;
    }

    public static boolean writeNbProject(NetbeansProject nbp) {
        boolean success = false;
        try (Connection con = getConnection()) {
            con.createStatement().executeUpdate(NetbeansProject.getSchema());
            String query
                    = "INSERT INTO " + NetbeansProject.TABLE_NAME
                      + " (" + NetbeansProject.FIELD_LIST + ") "
                      + "VALUES (?, ?, ?);";
            PreparedStatement insertStmt = con.prepareStatement(query);
            insertStmt.setString(1, nbp.getName());
            insertStmt.setString(2, nbp.getFolder());
            insertStmt.setString(3, nbp.getZip_file());
            insertStmt.executeUpdate();
            insertStmt.close();
            success = true;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error Writing Netbeans project...", ex);
        }
        return success;
    }

    public static NetbeansProject fetchNbProject(int id) {
        String selectQuery = "SELECT * FROM " + NetbeansProject.TABLE_NAME
                             + " WHERE id = " + id + ";";
        try (Connection con = getConnection();
             ResultSet rs = con.createStatement().executeQuery(selectQuery)) {
            // Always the first (and only) row
            if (rs.next()) {
                int ret_id = rs.getInt(1);
                String name = rs.getString(2);
                String folder = rs.getString(3);
                String zip_file = rs.getString(4);
                NetbeansProject proj =
                        new NetbeansProject(name, folder, zip_file);
                proj.setId(ret_id);
                return proj;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error Fetching CW...", ex);
        }
        return null;
    }

    public static void writeMySql(MySql mysql) {
        try (Connection con = getConnection()) {
            con.createStatement().executeUpdate(MySql.getSchema());
            String query
                    = "INSERT INTO " + MySql.TABLE_NAME
                      + "(" + MySql.FIELD_LIST + ") "
                      + "VALUES (?, ?)";
            PreparedStatement insertStmt = con.prepareStatement(query);
            insertStmt.setString(1, mysql.getName());
            insertStmt.setString(2, mysql.getSql_file());
            insertStmt.executeUpdate();
            insertStmt.close();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error Writing CW...", ex);
        }
    }

    private static Connection getConnection() {
        Connection con = null;
        int tries = 0;
        while (tries < 10) {
            try {
                con = DriverManager.getConnection("jdbc:sqlite:autdv2.db");
//                logger.log(Level.INFO, "Got DB Connection");
                break;
            } catch (SQLException ex) {
                tries++;
                logger.log(Level.INFO, "DB connection failed, retrying...", ex);
            }
        }
        if (con == null) {
            logger.log(Level.SEVERE, "Unable to get connection to DB");
        }
        return con;
    }
}
