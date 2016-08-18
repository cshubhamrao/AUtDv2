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

import com.github.cshubhamrao.AUtDv2.net.DriveUtils;
import com.github.cshubhamrao.AUtDv2.util.Log;
import com.github.cshubhamrao.AUtDv2.os.*;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;

/**
 * FXML Controller class
 *
 * @author shubham
 */
public class UIController implements Initializable {

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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.log(Level.INFO, "Initializing Controls");

        spinner_cwNo.setValueFactory(new IntegerSpinnerValueFactory(1, 199));
        cb_topic.setItems(FXCollections.observableArrayList("Java", "MySQL"));
        btn_NetBeans.setOnAction((e) -> new NetBeansRunner().run());
        btn_MySql.setOnAction((e) -> new MySqlRunner().run());
        btn_userAction.setOnAction((e) -> DriveUtils.upload(new File("log.txt")));
    }

}
