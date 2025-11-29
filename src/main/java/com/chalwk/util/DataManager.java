// JCBudgetBuddy
// Copyright (c) 2025 Jericho Crosby (Chalwk)
// Licensed under the MIT License.

package com.chalwk.util;

import com.chalwk.model.UserData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DataManager {
    private static DataManager instance;
    private final ObjectMapper objectMapper;
    private final Path dataFilePath;

    private DataManager() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        String userHome = System.getProperty("user.home");
        dataFilePath = Paths.get(userHome, ".JCBudgetBuddy", "userdata.json");
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public UserData loadUserData() {
        try {
            if (Files.exists(dataFilePath)) {
                return objectMapper.readValue(dataFilePath.toFile(), UserData.class);
            }
        } catch (IOException e) {
            System.err.println("Error loading user data: " + e.getMessage());
        }
        return new UserData(null, null, null, null, null, 0.0);
    }

    public void saveUserData(UserData userData) {
        try {
            Files.createDirectories(dataFilePath.getParent());
            objectMapper.writeValue(dataFilePath.toFile(), userData);
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
        }
    }

    public void exportData(UserData userData, File file) throws IOException {
        objectMapper.writeValue(file, userData);
    }

    public UserData importData(File file) throws IOException {
        return objectMapper.readValue(file, UserData.class);
    }
}