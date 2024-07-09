package com.example.financialmanagerapp.model.response;

public class ResponseObject<T> {


    protected int status;
    protected String message;
    protected T result;

    public ResponseObject(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public T getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }
}
