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
package com.github.cshubhamrao.AUtDv2.gui;

import com.github.cshubhamrao.AUtDv2.net.GoogleDriveTask;
import com.github.cshubhamrao.AUtDv2.os.CreateZipTask;
import com.github.cshubhamrao.AUtDv2.os.OSLib;
import com.github.cshubhamrao.AUtDv2.os.runners.MySqlDumpRunner;
import com.github.cshubhamrao.AUtDv2.os.runners.MySqlImportRunner;
import com.github.cshubhamrao.AUtDv2.os.runners.MySqlRunner;
import com.github.cshubhamrao.AUtDv2.os.runners.NetBeansRunner;
import com.github.cshubhamrao.AUtDv2.util.Log;
import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

/**
 * FXML Controller class. for {@code MainUI.fxml}
 *
 * @author Shubham Rao
 */
public class UIController {

    private static final java.util.logging.Logger logger = Log.logger;

    @FXML
    private Button btn_userAction;
    @FXML
    private ChoiceBox<String> cb_topic;
    @FXML
    private Button btn_NetBeans;
    @FXML
    private Button btn_MySql;
    @FXML
    private Spinner<Integer> spinner_cwNo;
    @FXML
    private Button btn_backup;
    @FXML
    private Button btn_restore;
    @FXML
    private TextField txt_dbBackup;
    @FXML
    private TextField txt_dbRestore;
    @FXML
    private PasswordField txt_mySqlPass;
    @FXML
    private Button btn_browse;
    @FXML
    private TextField txt_location;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final GoogleDriveTask gDriveTask = new GoogleDriveTask();

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        logger.log(Level.INFO, "Initializing Controls");

        if (OSLib.getCurrentArchitecture() == OSLib.Architecture.UNKNOWN
                || OSLib.getCurrentOS() == OSLib.OperatingSystem.UNKNOWN) {

            new Alert(Alert.AlertType.ERROR,
                    "Unable to determine current OS and/or System "
                    + "Architecture. Any OS-dependent functionality will not work")
                    .showAndWait();
            logger.log(Level.SEVERE, "Unable to detect OS and/or architecture reliably");
            logger.log(Level.CONFIG, OSLib.getCurrentArchitecture().toString());
            logger.log(Level.CONFIG, OSLib.getCurrentOS().toString());

            btn_NetBeans.setDisable(true);
            btn_MySql.setDisable(true);
            btn_backup.setDisable(true);
            btn_restore.setDisable(true);
        }

        spinner_cwNo.setValueFactory(new IntegerSpinnerValueFactory(1, 199));
        cb_topic.setItems(FXCollections.observableArrayList("Java", "MySQL"));

        btn_NetBeans.setOnAction(e -> executor.execute(new NetBeansRunner()));

        btn_MySql.setOnAction(e -> executor.execute(new MySqlRunner()));

        btn_userAction.setOnAction(e -> {
            GoogleDriveTask.UploadTask task = gDriveTask.new UploadTask(new File("log.txt"),
                    "Log File created by AUtDv2");
            Future<String> resp = executor.submit(task);
            checkSuccess(resp);
        });

        btn_backup.setOnAction(this::btn_backup_handler);
        btn_restore.setOnAction(this::btn_restore_handler);

        btn_browse.setOnAction(this::btn_browse_handler);

    }

    private void btn_backup_handler(ActionEvent e) {
        String dbName = txt_dbBackup.getText().trim();
        String password = txt_mySqlPass.getText();
        if (password.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Empty password").showAndWait();
        }
        if (dbName.isEmpty() || dbName.contains(" ")) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Invalid name for Database");
            alert.showAndWait();
        } else {
            executor.execute(new MySqlDumpRunner(dbName, password));
            GoogleDriveTask.UploadTask task = gDriveTask.new UploadTask(new File(dbName + ".sql"),
                    "Backup of Database: " + dbName);
            Future<String> resp = executor.submit(task);
            checkSuccess(resp);
        }
    }

    private void checkSuccess(Future<String> resp) {
        new Thread(() -> {
            try {
                if (resp.get() == null) {
                    new Alert(Alert.AlertType.ERROR, "Authorization Failed. Please try again")
                            .showAndWait();
                }
            } catch (InterruptedException | ExecutionException ex) {
                logger.log(Level.SEVERE, "Error checking for success", ex);
            }
        }, "Result Check Thread").start();
    }

    private void btn_restore_handler(ActionEvent e) {
        String dbName = txt_dbRestore.getText().trim();
        String password = txt_mySqlPass.getText();
        if (password.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Empty password").showAndWait();
        }
        File sqlFile = Paths.get(dbName + ".sql").toFile();
        if (dbName.isEmpty() || dbName.contains(" ")) {
            new Alert(Alert.AlertType.WARNING, "Invalid name for Database").showAndWait();
            return;
        }
        if (sqlFile.exists() && sqlFile.canRead()) {
            executor.execute(new MySqlImportRunner(sqlFile.getAbsolutePath(), dbName, password));
        } else {
            new Alert(Alert.AlertType.ERROR,
                    ".sql file containing backup of Database \"" + dbName + "\" not found.\n"
                    + "Tip: To restore to another Database, rename the .sql file to desired "
                    + "Database's name.\n"
                    + "File name: " + sqlFile.getAbsolutePath())
                    .showAndWait();
        }
    }

    private void btn_browse_handler(ActionEvent e) {
        DirectoryChooser dir = new DirectoryChooser();
        File f = dir.showDialog(null);
        txt_location.setText(f.toString());
        executor.submit(new CreateZipTask(f));
    }
}
