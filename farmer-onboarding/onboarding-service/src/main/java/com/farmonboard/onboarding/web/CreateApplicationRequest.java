package com.farmonboard.onboarding.web;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

// The JSON we receive: {"aadhaarNumber": "123412341234"}
// @NotNull: the field must be there. (@Pattern lets a missing value through, so we need both.)
// @Pattern: exactly 12 digits. \d means "a digit", {12} means "exactly 12 of them".
public record CreateApplicationRequest(
        @NotNull
        @Pattern(regexp = "\\d{12}", message = "must be exactly 12 digits")
        String aadhaarNumber) {
}
