package com.company.customersearch.exception;

public class ExternalApiException extends RuntimeException {

    private final Integer statusCode;

    public ExternalApiException(String message) {
        super(message);
        this.statusCode = null;
    }

    public ExternalApiException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = null;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
