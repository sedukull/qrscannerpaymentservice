package com.bangtechnologies.qrscanner.errors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Component
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

        @ExceptionHandler(value
                = { RuntimeException.class })
        protected ResponseEntity<Object> handleConflict(
                RuntimeException ex, WebRequest request) {
            String bodyOfResponse = "Oops application specific error. We will triage and fix it soon";
            return handleExceptionInternal(ex, bodyOfResponse,
                    new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
}
