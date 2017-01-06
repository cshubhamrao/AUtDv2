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

import com.github.cshubhamrao.AUtDv2.db.DatabaseTasks;
import com.github.cshubhamrao.AUtDv2.models.ClassWork;
import com.github.cshubhamrao.AUtDv2.models.ClassWork.Topic;
import com.github.cshubhamrao.AUtDv2.net.GoogleDrive;
import com.github.cshubhamrao.AUtDv2.net.UploadTask;
import com.github.cshubhamrao.AUtDv2.net.UserInfoTask;
import com.github.cshubhamrao.AUtDv2.os.CreateZipTask;
import com.github.cshubhamrao.AUtDv2.os.OSLib;
import com.github.cshubhamrao.AUtDv2.os.runners.MySqlDumpRunner;
import com.github.cshubhamrao.AUtDv2.os.runners.MySqlImportRunner;
import com.github.cshubhamrao.AUtDv2.os.runners.MySqlRunner;
import com.github.cshubhamrao.AUtDv2.os.runners.NetBeansRunner;
import com.github.cshubhamrao.AUtDv2.util.Log;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;

/**
 * FXML Controller class. for {@code MainUI.fxml}
 *
 * @author Shubham Rao
 */
public class UIController {

    private static final java.util.logging.Logger logger = Log.logger;

    @FXML
    private ImageView img_user;
    @FXML
    private Label txt_welcome;
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
    private Button btn_m_backup;
    @FXML
    private Button btn_n_backup;
    @FXML
    private Button btn_m_restore;
    @FXML
    private TextField txt_projName;
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
    @FXML
    private Button btn_cwSave;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextArea txt_desc;

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private DatabaseTasks db;
    private boolean signedIn = false;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        logger.log(Level.INFO, "Initializing Controls");

        if (OSLib.getCurrentArchitecture() == OSLib.Architecture.UNKNOWN
                || OSLib.getCurrentOS() == OSLib.OperatingSystem.UNKNOWN) {

            new Alert(Alert.AlertType.ERROR,
                    "Unable to determine current OS and/or System Architecture."
                    + " Any OS-dependent functionality will not work")
                    .showAndWait();
            logger.log(Level.SEVERE, "Unable to detect OS and/or architecture"
                    + " reliably");
            logger.log(Level.CONFIG, OSLib.getCurrentArchitecture().toString());
            logger.log(Level.CONFIG, OSLib.getCurrentOS().toString());

            btn_NetBeans.setDisable(true);
            btn_MySql.setDisable(true);
            btn_m_backup.setDisable(true);
            btn_m_restore.setDisable(true);
        }

        spinner_cwNo.setValueFactory(new IntegerSpinnerValueFactory(1, 199));
        cb_topic.setItems(FXCollections.observableArrayList("Java", "MySQL"));

        btn_NetBeans.setOnAction(e -> executor.submit(new NetBeansRunner()));

        btn_MySql.setOnAction(e -> executor.submit(new MySqlRunner()));

        btn_userAction.setOnAction(this::setUserInfo);

        btn_m_backup.setOnAction(this::btn_backup_handler);
        btn_m_restore.setOnAction(this::btn_restore_handler);

        btn_browse.setOnAction((e) -> {
            DirectoryChooser dir = new DirectoryChooser();
            File f = dir.showDialog(null);
            txt_location.setText(f.toString());
        });
        btn_n_backup.setOnAction(this::btn_n_backup_handler);

        btn_cwSave.setOnAction(this::btn_cwSave_handler);
    }

    private void btn_cwSave_handler(ActionEvent e) {
        int cw = spinner_cwNo.getValue();
        LocalDate date = datePicker.getValue();
        ClassWork.Topic topic = null;
        switch (cb_topic.getValue()) {
            case "Java":
                topic = Topic.JAVA;
                break;
            case "MySQL":
                topic = Topic.MYSQL;
                break;
        }
        String desc = txt_desc.getText();

        ClassWork clswrk = new ClassWork(cw, date, topic, desc);

        try {
            db = new DatabaseTasks();
            db.writeCW(clswrk);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Database Error", ex);
        }
    }

    private void setUserInfo(ActionEvent e) {
        if (signedIn) {
            GoogleDrive.invalidate();
            btn_userAction.setText("Sign In");
            img_user.setImage(null);
            txt_welcome.setText("Welcome! Click 'Sign In' to backup to "
                    + "Google Drive");
            signedIn = false;
        } else {
            signedIn = true;
            btn_userAction.setText("Sign Out");
            logger.log(Level.INFO, "Gathering user info");
            Future<List<String>> resp = executor.submit(new UserInfoTask());

            new Thread(() -> {
                List<String> info;
                try {
                    info = resp.get();
                    Platform.runLater(() -> {
                        txt_welcome.setText("Welcome " + info.get(0) + "! "
                                + "Have a nice Day.");
                        img_user.setImage(new Image(info.get(1),
                                64, 64, true, true, true));
                    });
                } catch (InterruptedException | ExecutionException ex) {
                    logger.log(Level.SEVERE, "Error getting user info", ex);
                }
            }, "User info thread").start();
        }
    }

    private void btn_backup_handler(ActionEvent e) {
        String dbName = txt_dbBackup.getText().trim();
        String password = txt_mySqlPass.getText();
        if (password.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Empty password").showAndWait();
        }
        if (dbName.isEmpty() || dbName.contains(" ")) {
            new Alert(Alert.AlertType.WARNING, "Invalid name for Database")
                    .showAndWait();
        } else {
            Future<Integer> resp = executor.submit(new MySqlDumpRunner(dbName,
                    password));
            uploadSql(resp, dbName);
        }
    }

    private void btn_restore_handler(ActionEvent e) {
        String dbName = txt_dbRestore.getText().trim();
        String password = txt_mySqlPass.getText();
        if (password.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Empty password").showAndWait();
        }
        File sqlFile = Paths.get(dbName + ".sql").toFile();
        if (dbName.isEmpty() || dbName.contains(" ")) {
            new Alert(Alert.AlertType.WARNING, "Invalid name for Database")
                    .showAndWait();
            return;
        }
        if (sqlFile.exists() && sqlFile.canRead()) {
            Future<Integer> resp = executor.submit(
                    new MySqlImportRunner(sqlFile.getAbsolutePath(), dbName,
                            password));
            checkImportSuccess(resp);
        } else {
            new Alert(Alert.AlertType.ERROR,
                    ".sql file containing backup of Database \"" + dbName
                    + "\" not found.\n"
                    + "Tip: To restore to another Database, rename the .sql"
                    + " file to desired "
                    + "Database's name.\n"
                    + "File name: " + sqlFile.getAbsolutePath())
                    .showAndWait();
        }
    }

    private void btn_n_backup_handler(ActionEvent e) {
        File f = new File(txt_location.getText());
        Future<Path> result = executor.submit(new CreateZipTask(f));
        uploadZip(result);
    }

    private void uploadZip(Future<Path> resp) {
        new Thread(() -> {
            try {
                File f = resp.get().toFile();
                if (f.exists()) {
                    UploadTask task = new UploadTask(f,
                            "Project Backup created by AUtDv2");
                    Future<String> res = executor.submit(task);
                    checkDriveSuccess(res);
                } else {
                    Platform.runLater(()
                            -> new Alert(Alert.AlertType.ERROR,
                                    "Creating zip file failed. "
                                    + "Please try again").showAndWait());
                }
            } catch (InterruptedException | ExecutionException ex) {
                logger.log(Level.SEVERE, "Error checking for success", ex);
            }
        }, "Upload zip Thread").start();
    }

    private void uploadSql(Future<Integer> resp, String dbName) {
        new Thread(() -> {
            try {
                int exit = resp.get();
                if (exit == 0) {
                    Platform.runLater(()
                            -> new Alert(Alert.AlertType.INFORMATION,
                                    "Database backup created")
                            .show());
                    UploadTask task = new UploadTask(
                            new File(dbName + ".sql"),
                            "Backup of Database: " + dbName);
                    Future<String> authResp = executor.submit(task);
                    checkDriveSuccess(authResp);
                } else {
                    Platform.runLater(()
                            -> new Alert(Alert.AlertType.ERROR,
                                    "Unable to create Backup of database")
                            .show());
                }
            } catch (InterruptedException | ExecutionException ex) {
                logger.log(Level.SEVERE, "Error in sql backup", ex);
            }
        }, "DB Upload Thread").start();
    }

    private void checkImportSuccess(Future<Integer> resp) {
        new Thread(() -> {
            try {
                int exitCode = resp.get();
                if (exitCode == 0) {
                    Platform.runLater(()
                            -> new Alert(Alert.AlertType.INFORMATION,
                                    "Database successfully restored").show());
                } else {
                    Platform.runLater(()
                            -> new Alert(Alert.AlertType.ERROR,
                                    "Database restore unsuccessful").show());
                }
            } catch (InterruptedException | ExecutionException ex) {
                logger.log(Level.SEVERE, "Error importing DB", ex);
            }
        }, "Import DB thread").start();
    }

    private void checkDriveSuccess(Future<String> resp) {
        new Thread(() -> {
            try {
                String fileId = resp.get();
                if (fileId == null) {
                    Platform.runLater(()
                            -> new Alert(Alert.AlertType.ERROR,
                                    "Upload to Google Drive failed. "
                                    + "Please try again").showAndWait());
                } else {
                    Platform.runLater(()
                            -> new Alert(Alert.AlertType.INFORMATION,
                                    "File Uploaded").show());
                }
            } catch (InterruptedException | ExecutionException ex) {
                logger.log(Level.SEVERE, "Error checking for success", ex);
            }
        }, "Upload Check Thread").start();
    }
}
