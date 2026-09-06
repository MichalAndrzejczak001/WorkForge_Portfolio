package com.workforge.jobservice.application.exception;

public class InvalidJobStatusException extends RuntimeException {
    public InvalidJobStatusException(String message) {
        super(message);
    }
}
