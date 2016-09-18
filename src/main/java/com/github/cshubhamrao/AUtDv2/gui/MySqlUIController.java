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
package com.github.cshubhamrao.AUtDv2.gui;

import java.util.Arrays;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javax.imageio.ImageIO;

/**
 * FXML Controller class
 *
 * @author "Shubham Rao <cshubhamrao@gmail.com>"
 */
public class MySqlUIController {

    @FXML
    private ImageView img_main;

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        img_main.setPreserveRatio(true);
        img_main.setFitHeight(0);
        img_main.setFitWidth(0);
        img_main.setSmooth(false);
//        img_main.
//        img_main.setViewport(new Rectangle2D(600, 600, 600, 600));
        if (clipboard.hasImage()) {
//            Toolkit.getDefaultToolkit().getSystemClipboard().

//            ImageIO.write(clipboard.getImage(), "png", new File("abc.png"));
            System.out.println(Arrays.toString(ImageIO.getWriterFormatNames()));
        }
    }

}
