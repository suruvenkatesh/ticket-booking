package com.farmonboard.onboarding.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        // One application per Aadhaar. We check first, to give a clear error.
        // This is "check, then act": two requests at the same moment can both pass the check.
        // Then the database's UNIQUE rule refuses the second one. That rule is the real guard.
        if (repository.existsByAadhaarNumber(aadhaarNumber)) {
            throw new DuplicateAadhaarException();
        }
        return repository.save(new FarmerApplication(aadhaarNumber));
    }

    // Finds one application, or throws ApplicationNotFoundException (a 404) if there's none.
    // findById returns an Optional: a box that either holds the application or is empty.
    // It makes us handle the "not found" case here, instead of passing a null around.
    public FarmerApplication findById(Long id) {
        Optional<FarmerApplication> application = repository.findById(id);
        if (application.isEmpty()) {
            throw new ApplicationNotFoundException(id);
        }
        return application.get();
    }

    // One page of applications. The Pageable says which page, how many per page, and the order.
    // Spring Data runs two queries: one for the rows of this page, one to count all rows.
    public Page<FarmerApplication> findPage(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
