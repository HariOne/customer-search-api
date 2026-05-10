package com.company.customersearch.exception;

public class InvalidBrandException extends RuntimeException {

    public InvalidBrandException(String message) {
        super(message);
    }

    public InvalidBrandException(String message, Throwable cause) {
        super(message, cause);
    }
}
