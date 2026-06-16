package com.horseracing.exception;

public class ResourceNotFoundException extends CustomException {
    public ResourceNotFoundException(String message) {
        super(404, message);
    }
}
