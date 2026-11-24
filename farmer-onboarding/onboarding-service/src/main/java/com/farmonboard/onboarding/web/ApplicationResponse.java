package com.farmonboard.onboarding.web;

import java.time.Instant;

import com.farmonboard.onboarding.domain.ApplicationStatus;
import com.farmonboard.onboarding.domain.FarmerApplication;

// The JSON we send back for an application. Only what the app needs, and never the full Aadhaar.
public record ApplicationResponse(
        Long id,
        String inceptionNumber,
        String maskedAadhaar,
        ApplicationStatus status,
        Instant createdAt) {

    // Turns the entity into this DTO.
    public static ApplicationResponse from(FarmerApplication application) {
        return new ApplicationResponse(
                application.getId(),
                // The id as a 10-digit number padded with zeros, like the real app: 4 -> "0000000004".
                String.format("%010d", application.getId()),
                mask(application.getAadhaarNumber()),
                application.getStatus(),
                application.getCreatedAt());
    }

    // Shows only the last 4 digits, as UIDAI's rules require: "XXXXXXXX1234".
    private static String mask(String aadhaarNumber) {
        return "XXXXXXXX" + aadhaarNumber.substring(aadhaarNumber.length() - 4);
    }
}
