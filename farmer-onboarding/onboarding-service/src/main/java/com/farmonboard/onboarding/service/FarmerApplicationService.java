package com.farmonboard.onboarding.service;

import org.springframework.stereotype.Service;

import com.farmonboard.onboarding.domain.FarmerApplication;
import com.farmonboard.onboarding.repository.FarmerApplicationRepository;

// The business rules for farmer applications. The controller calls this; this calls the repository.
@Service
public class FarmerApplicationService {

    private final FarmerApplicationRepository repository;

    // Constructor injection: Spring passes in the repository it built from our interface.
    public FarmerApplicationService(FarmerApplicationRepository repository) {
        this.repository = repository;
    }

    // Creates a new DRAFT application for a farmer whose Aadhaar was verified.
    // (The fake Aadhaar OTP check comes in front of this in a later step.)
    // save() returns the saved application, now with the id the database gave it.
    public FarmerApplication create(String aadhaarNumber) {
        return repository.save(new FarmerApplication(aadhaarNumber));
    }
}
