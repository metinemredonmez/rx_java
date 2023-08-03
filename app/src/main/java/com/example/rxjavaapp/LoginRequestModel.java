package com.example.rxjavaapp;

import com.google.gson.annotations.SerializedName;

public class LoginRequestModel {
    @SerializedName("LANGUAGE")
    private String language;
    @SerializedName("CLIENT_SESSION_ID")
    private String clientSessionId;

    public LoginRequestModel(String language, String clientSessionId) {
        this.language = language;
        this.clientSessionId = clientSessionId;
    }

    public String getLanguage() {
        return language;
    }

    public String getClientSessionId() {
        return clientSessionId;
    }
}

