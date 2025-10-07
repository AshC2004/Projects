package com.example.alerting.exception;

public class AlertNotFoundException extends RuntimeException {
    public AlertNotFoundException(String message) { super(message); }
}
