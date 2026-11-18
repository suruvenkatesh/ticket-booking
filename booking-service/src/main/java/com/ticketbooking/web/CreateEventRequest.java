package com.ticketbooking.web;

import jakarta.validation.constraints.NotBlank;

// A DTO for the JSON we receive: {"name": "Comedy Night"}
// Jackson turns that JSON into this record (step 4 in reverse).
// @NotBlank: the name must not be missing, empty, or only spaces.
public record CreateEventRequest(@NotBlank String name) {
}
