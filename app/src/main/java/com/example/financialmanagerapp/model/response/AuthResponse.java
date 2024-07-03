package com.example.financialmanagerapp.model.response;

import com.example.financialmanagerapp.model.User;

import java.util.List;

public class AuthResponse {
    protected User user;
    protected String token;
    protected String _refresh_token;

    protected List<String> errors;
    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public String get_refresh_token() {
        return _refresh_token;
    }

    public List<String> getErrors() {
        return errors;
    }
}
