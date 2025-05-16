package com.neoinvo.invoice.dto;

public class OTPResponse {
    private boolean verified;
    private String message;
    private String token;

    public OTPResponse() {}

    public OTPResponse(boolean verified, String message) {
        this.verified = verified;
        this.message = message;
    }

    public OTPResponse(boolean verified, String message, String token) {
        this.verified = verified;
        this.message = message;
        this.token = token;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
