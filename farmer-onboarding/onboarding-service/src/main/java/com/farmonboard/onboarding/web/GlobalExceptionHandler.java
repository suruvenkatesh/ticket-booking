package com.farmonboard.onboarding.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.farmonboard.onboarding.service.DuplicateAadhaarException;

// Turns exceptions from any controller into proper HTTP replies, all in one place.
// @RestControllerAdvice: applies to every @RestController, and what we return is written as JSON.
// @ExceptionHandler(X.class): "when a controller throws X, call this method instead".
// The replies use ProblemDetail, the standard error format for HTTP APIs (RFC 9457):
//   {"type": "about:blank", "title": "Conflict", "status": 409, "detail": "...", "instance": "/api/applications"}
// Rule for every handler here: never put personal data (like an Aadhaar number) in a reply.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Our own check found an application for this Aadhaar.
    @ExceptionHandler(DuplicateAadhaarException.class)
    public ProblemDetail handleDuplicate(DuplicateAadhaarException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    // The database refused a change that breaks one of its rules. Example: two requests for the
    // same Aadhaar arrive at the same moment, both pass our check, and UNIQUE stops the second.
    // The detail is general on purpose: the database's own message can contain personal data.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "The request conflicts with existing data");
    }

    // @Valid failed. We name each field and the rule it broke, but never the rejected value.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, String.join("; ", errors));
    }
}
