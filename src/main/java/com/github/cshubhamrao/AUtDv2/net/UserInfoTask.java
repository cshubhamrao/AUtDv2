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
package com.github.cshubhamrao.AUtDv2.net;

import com.github.cshubhamrao.AUtDv2.util.Log;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author "Shubham Rao (cshubhamrao@gmail.com)"
 */
public class UserInfoTask extends GoogleDrive implements Callable<List<String>> {

    private static Logger logger = Log.logger;

    private Drive service;

    @Override
    public List<String> call() throws Exception {
        if (service == null) {
            service = super.getService();
        }
        List<String> out = new ArrayList<>();
        logger.log(Level.INFO, "Getting User Info");
        User user = service.about().get().setFields("user").execute().getUser();
        out.add(user.getDisplayName());
        out.add(user.getPhotoLink());
        return out;
    }

}
