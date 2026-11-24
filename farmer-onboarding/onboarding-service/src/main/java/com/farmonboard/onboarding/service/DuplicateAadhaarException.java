package com.farmonboard.onboarding.service;

// Thrown when an application already exists for an Aadhaar number.
// Unchecked (extends RuntimeException): callers don't have to catch it.
// The global error handler turns it into a 409 Conflict reply.
// The message never contains the Aadhaar number.
public class DuplicateAadhaarException extends RuntimeException {

    public DuplicateAadhaarException() {
        super("An application already exists for this Aadhaar number");
    }
}
