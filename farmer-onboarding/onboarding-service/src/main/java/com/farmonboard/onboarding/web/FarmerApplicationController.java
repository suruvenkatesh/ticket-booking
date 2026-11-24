package com.farmonboard.onboarding.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.farmonboard.onboarding.domain.FarmerApplication;
import com.farmonboard.onboarding.service.FarmerApplicationService;

import jakarta.validation.Valid;

// Answers web requests about farmer applications. Only web work here: JSON, @Valid, status codes.
@RestController
public class FarmerApplicationController {

    private final FarmerApplicationService service;

    public FarmerApplicationController(FarmerApplicationService service) {
        this.service = service;
    }

    // POST http://localhost:8081/api/applications with {"aadhaarNumber": "123412341234"}.
    // @Valid checks the request's rules first; if one fails, Spring replies 400 and this never runs.
    // Replies 201 Created with the new application.
    @PostMapping("/api/applications")
    public ResponseEntity<ApplicationResponse> create(@Valid @RequestBody CreateApplicationRequest request) {
        FarmerApplication application = service.create(request.aadhaarNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApplicationResponse.from(application));
    }
}
