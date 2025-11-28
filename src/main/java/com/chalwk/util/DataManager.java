package com.chalwk.util;

import com.chalwk.model.UserData;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;

public class DataManager {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static DataManager instance;

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule()); // Add this line to handle Java 8 date/time types
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optional: makes dates more readable
    }

    private DataManager() {
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public UserData loadUserData() {
        try {
            File file = new File(FileUtil.getUserDataFilePath());
            if (file.exists()) {
                return mapper.readValue(file, UserData.class);
            }
        } catch (IOException e) {
            System.err.println("Error loading user data: " + e.getMessage());
        }
        return new UserData(null, null, null, null, null, 0.0);
    }

    public void saveUserData(UserData userData) {
        try {
            FileUtil.ensureDataDirectoryExists();
            File file = new File(FileUtil.getUserDataFilePath());
            mapper.writeValue(file, userData);
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
        }
    }

    public void exportData(UserData userData, File file) throws IOException {
        mapper.writeValue(file, userData);
    }

    public UserData importData(File file) throws IOException {
        return mapper.readValue(file, UserData.class);
    }
}