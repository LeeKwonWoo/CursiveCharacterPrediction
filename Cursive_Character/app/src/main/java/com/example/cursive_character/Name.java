package com.example.cursive_character;

public class Name {
    private static Name instance = new Name();

    private String ccName;

    private Name() {}
    public static Name getInstance() {
        if (instance == null) {
            instance = new Name();
        }
        return instance;
    }
    public String getCcName() {
        return ccName;
    }
    public void setCcName(String ccName) {
        this.ccName = ccName;
    }
}
