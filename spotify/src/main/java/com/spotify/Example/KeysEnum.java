package com.spotify.Example;

public enum KeysEnum {
    CLIENT_ID("hide"),
    CLIENT_SECRET("hide");

    private final String key;

    KeysEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
