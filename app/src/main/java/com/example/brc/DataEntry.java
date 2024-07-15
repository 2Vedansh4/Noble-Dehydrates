package com.example.brc;

public class DataEntry {
    private String currentTime;
    private String intentData;
    private String userEmail;
    private String userInput;

    public DataEntry(String currentTime, String intentData, String userEmail, String userInput) {
        this.currentTime = currentTime;
        this.intentData = intentData;
        this.userEmail = userEmail;
        this.userInput = userInput;
    }

    // Getters and setters
    public String getCurrentTime() {
        return currentTime;
    }

    public String getIntentData() {
        return intentData;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserInput() {
        return userInput;
    }
}
