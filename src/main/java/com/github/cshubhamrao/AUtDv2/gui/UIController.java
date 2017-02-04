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
import com.github.cshubhamrao.AUtDv2.models.Classwork;
import com.github.cshubhamrao.AUtDv2.models.Classwork.Topic;
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
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.util.StringConverter;

/**
 * FXML Controller class. for {@code MainUI.fxml}
 *
 * @author Shubham Rao
 */
public class UIController {

    private static final java.util.logging.Logger logger = Log.logger;

    @FXML
    private Button btn_runMySql;
    @FXML
    private Button btn_runNetBeans;
    @FXML
    private Button btn_nbBrowse;
    @FXML
    private Button btn_cwInsert;
    @FXML
    private Button btn_cwUpdate;
    @FXML
    private Button btn_dbBackup;
    @FXML
    private Button btn_dbRestore;
    @FXML
    private Button btn_nbBackup;
    @FXML
    private Button btn_userAction;
    @FXML
    private ChoiceBox<String> cb_topic;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ImageView img_user;
    @FXML
    private Spinner<Integer> spinner_cwNo;
    @FXML
    private TextField txt_dbBackup;
    @FXML
    private TextField txt_dbRestore;
    @FXML
    private TextArea txt_desc;
    @FXML
    private TextField txt_location;
    @FXML
    private PasswordField txt_mySqlPass;
    @FXML
    private TextField txt_projName;
    @FXML
    private Label txt_welcome;

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private int presentRow = 0;
    private boolean signedIn = false;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        logger.log(Level.INFO, "Initializing Controls");
        setInitialValues();
        checkPlatform();
        setHandlers();
    }

    private void setInitialValues() {
        IntegerSpinnerValueFactory factory
                = new IntegerSpinnerValueFactory(1, Integer.MAX_VALUE);
        TextFormatter formatter = new TextFormatter(factory.getConverter(),
                                                    factory.getValue());
        spinner_cwNo.getEditor().setTextFormatter(formatter);
        factory.valueProperty().bindBidirectional(formatter.valueProperty());
        spinner_cwNo.setValueFactory(factory);
        spinner_cwNo.getValueFactory().setValue(0);
//        spinner_cwNo.setValueFactory(new IntegerSpinnerValueFactory(1, 199));
        cb_topic.setItems(FXCollections.observableArrayList("Java", "MySQL"));
        btn_cwUpdate.setDisable(true);
    }

    private void checkPlatform() {
        if (OSLib.getCurrentArchitecture() == OSLib.Architecture.UNKNOWN
            || OSLib.getCurrentOS() == OSLib.OperatingSystem.UNKNOWN) {

            new Alert(Alert.AlertType.ERROR,
                      "Unable to find current OS and/or system architecture. "
                      + "Any OS-dependent functionality will not work")
                    .showAndWait();
            logger.log(Level.SEVERE, "Unable to detect OS and/or architecture"
                                     + " reliably");
            logger.log(Level.CONFIG, OSLib.getCurrentArchitecture().toString());
            logger.log(Level.CONFIG, OSLib.getCurrentOS().toString());

            btn_runNetBeans.setDisable(true);
            btn_runMySql.setDisable(true);
            btn_dbBackup.setDisable(true);
            btn_dbRestore.setDisable(true);
        }
    }

    private void setHandlers() {
        btn_runNetBeans.setOnAction(e -> executor.submit(new NetBeansRunner()));
        btn_runMySql.setOnAction(e -> executor.submit(new MySqlRunner()));

        btn_nbBrowse.setOnAction(e -> {
            DirectoryChooser dir = new DirectoryChooser();
            File f = dir.showDialog(null);
            txt_location.setText(f.toString());
        });

        btn_userAction.setOnAction(this::btn_userAction_handler);
        btn_dbBackup.setOnAction(this::btn_dbBackup_handler);
        btn_dbRestore.setOnAction(this::btn_dbRestore_handler);
        btn_nbBackup.setOnAction(this::btn_nbBackup_handler);
        btn_cwInsert.setOnAction(this::btn_cwInsert_handler);
        btn_cwUpdate.setOnAction(this::btn_cwUpdate_handler);
        spinner_cwNo.valueProperty().addListener(this::fetchClasswork);
    }

    private void btn_dbBackup_handler(ActionEvent e) {
        String dbName = txt_dbBackup.getText().trim();
        String password = txt_mySqlPass.getText();
        if (password.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Empty password").showAndWait();
        }
        if (dbName.isEmpty() || dbName.contains(" ")) {
            new Alert(Alert.AlertType.WARNING, "Invalid name for Database")
                    .showAndWait();
        } else {
            Future<Integer> resp = executor.submit(
                    new MySqlDumpRunner(dbName, password)
            );
            uploadSql(resp, dbName);
        }
    }

    private void btn_cwInsert_handler(ActionEvent e) {
        new Thread(() -> {
            presentRow = DatabaseTasks.writeCW(getCw());
            if (presentRow == 0) {
                Platform.runLater(()
                        -> new Alert(Alert.AlertType.ERROR,
                                     "Error Writing to DB").show());
            } else {
                Platform.runLater(() -> {
                    btn_cwUpdate.setDisable(false);
                    btn_cwInsert.setDisable(true);
                });
            }
        }).start();
    }

    private void btn_cwUpdate_handler(ActionEvent e) {
        new Thread(() -> {
            DatabaseTasks.updateCW(presentRow, getCw());
            if (presentRow == 0) {
                Platform.runLater(()
                        -> new Alert(Alert.AlertType.ERROR,
                                     "Error Writing to DB").show());
            }
        }).start();
    }

    private void btn_nbBackup_handler(ActionEvent e) {
        File f = new File(txt_location.getText());
        Future<Path> result = executor.submit(new CreateZipTask(f));
        uploadZip(result);
    }

    private void btn_dbRestore_handler(ActionEvent e) {
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

    private <T> void commitEditorText(Spinner<T> spinner) {
        if (!spinner.isEditable()) {
            return;
        }
        String text = spinner.getEditor().getText();
        SpinnerValueFactory<T> valueFactory = spinner.getValueFactory();
        if (valueFactory != null) {
            StringConverter<T> converter = valueFactory.getConverter();
            if (converter != null) {
                T value = converter.fromString(text);
                valueFactory.setValue(value);
            }
        }
    }

    private void fetchClasswork(ObservableValue obs, int old, int newVal) {
        new Thread(() -> {
            Classwork cw = DatabaseTasks.fetchCW(newVal);

            Platform.runLater(() -> {
                if (cw != null) {
                    datePicker.setValue(cw.getDate());
                    cb_topic.setValue(cw.getTopic());
                    txt_desc.setText(cw.getDescription());
                    presentRow = cw.getRowID();
                    btn_cwInsert.setDisable(true);
                    btn_cwUpdate.setDisable(false);
                } else {
                    btn_cwUpdate.setDisable(true);
                    btn_cwInsert.setDisable(false);
                    datePicker.setValue(LocalDate.now());
                    cb_topic.setValue("Java");
                    txt_desc.setText("");
                }
            });
        }).start();
    }

    private Classwork getCw() {
        int cwno = spinner_cwNo.getValue();
        LocalDate date = datePicker.getValue();
        Classwork.Topic topic = null;
        switch (cb_topic.getValue()) {
            case "Java":
                topic = Topic.JAVA;
                break;
            case "MySQL":
                topic = Topic.MYSQL;
                break;
        }
        String desc = txt_desc.getText();
        Classwork cw = new Classwork(cwno, date, topic, desc);
        return cw;
    }

    private void btn_userAction_handler(ActionEvent e) {
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

    private void uploadZip(Future<Path> resp) {
        new Thread(() -> {
            try {
                File f = resp.get().toFile();
                if (f.exists()) {
                    UploadTask task
                            = new UploadTask(f, "Project Backup created by "
                                    + "AUtDv2");
                    Future<String> res = executor.submit(task);
                    checkDriveSuccess(res);
                } else {
                    Platform.runLater(() ->
                            new Alert(Alert.AlertType.ERROR, "Creating zip file"
                                    + " failed. Please try again")
                                    .showAndWait());
                }
            } catch (InterruptedException | ExecutionException ex) {
                logger.log(Level.SEVERE, "Error checking for success", ex);
            }
        }, "Upload zip Thread").start();
    }

}
