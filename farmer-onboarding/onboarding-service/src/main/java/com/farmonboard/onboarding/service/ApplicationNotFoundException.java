package com.farmonboard.onboarding.service;

// Thrown when there's no application with the given id.
// The global error handler turns it into a 404 Not Found reply.
public class ApplicationNotFoundException extends RuntimeException {

    public ApplicationNotFoundException(Long id) {
        super("No application with id " + id);
    }
}
