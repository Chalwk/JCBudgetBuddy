// JCBudgetBuddy
// Copyright (c) 2025 Jericho Crosby (Chalwk)
// Licensed under the MIT License.

package com.chalwk.util;

import java.io.File;
import java.nio.file.Paths;

public class FileUtil {

    public static String getUserDataDirectory() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, "JCBudgetBuddy").toString();
    }

    public static String getUserDataFilePath() {
        return Paths.get(getUserDataDirectory(), "userdata.json").toString();
    }

    public static void ensureDataDirectoryExists() {
        File directory = new File(getUserDataDirectory());
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IllegalStateException("Could not create data directory: " + directory.getAbsolutePath());
            }
        }
    }
}