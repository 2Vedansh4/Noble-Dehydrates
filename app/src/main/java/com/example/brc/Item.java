package com.example.brc;

import java.util.Map;

public class Item {
    private String name;
    private Map<String, Boolean> allowedRoles;

    // Default constructor required for Firebase deserialization
    public Item() {
    }

    public Item(String name, Map<String, Boolean> allowedRoles) {
        this.name = name;
        this.allowedRoles = allowedRoles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Boolean> getAllowedRoles() {
        return allowedRoles;
    }

    public void setAllowedRoles(Map<String, Boolean> allowedRoles) {
        this.allowedRoles = allowedRoles;
    }
}


