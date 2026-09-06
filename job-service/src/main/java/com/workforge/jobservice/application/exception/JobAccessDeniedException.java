package com.workforge.jobservice.application.exception;

public class JobAccessDeniedException extends RuntimeException {
    public JobAccessDeniedException(String message) {
        super(message);
    }
}
