package com.company.customersearch.exception;

import com.company.customersearch.model.ErrorResponse;
import com.company.customersearch.util.CorrelationIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @ExceptionHandler(InvalidBrandException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidBrandException(
            InvalidBrandException ex,
            WebRequest request) {

        String correlationId = CorrelationIdUtil.getCorrelationId();
        log.error("Invalid brand provided. Correlation ID: {}", correlationId, ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("INVALID_BRAND")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().format(formatter))
                .correlationId(correlationId)
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExternalApiException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ResponseEntity<ErrorResponse> handleExternalApiException(
            ExternalApiException ex,
            WebRequest request) {

        String correlationId = CorrelationIdUtil.getCorrelationId();
        log.error("External API error. Status Code: {}. Correlation ID: {}", 
                ex.getStatusCode(), correlationId, ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("EXTERNAL_API_ERROR")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().format(formatter))
                .correlationId(correlationId)
                .status(HttpStatus.BAD_GATEWAY.value())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            WebRequest request) {

        String correlationId = CorrelationIdUtil.getCorrelationId();
        log.error("Unexpected error occurred. Correlation ID: {}", correlationId, ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred. Please try again later.")
                .timestamp(LocalDateTime.now().format(formatter))
                .correlationId(correlationId)
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
