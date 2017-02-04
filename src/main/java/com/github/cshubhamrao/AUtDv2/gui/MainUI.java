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
package com.github.cshubhamrao.AUtDv2.gui;

import com.github.cshubhamrao.AUtDv2.util.Log;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Main GUI class for application. This class loads the {@code MainUI.fxml} file
 * and displays the rendered GUI.
 *
 * @see UIController
 * @author Shubham Rao
 */
public class MainUI extends Application {

    private static final java.util.logging.Logger logger = Log.logger;

    @Override
    public void start(Stage primaryStage) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/gui/MainUI.fxml"));
            primaryStage.setScene(new Scene(root));
        } catch (IOException ex) {
            logger.log(java.util.logging.Level.SEVERE,
                       "Error in loading main FXML", ex);
            new Alert(Alert.AlertType.ERROR,
                      "Fatal Error in loading GUI. Exiting...").showAndWait();
            Platform.exit();
        }

        primaryStage.setTitle("Auto Upload to Drive v2");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        logger.info("LOGGING Started");
        launch(args);
    }

}
