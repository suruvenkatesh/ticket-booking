package com.ticketbooking.web;

// A DTO for the JSON we receive: {"name": "Comedy Night"}
// Jackson turns that JSON into this record (step 4 in reverse).
public record CreateEventRequest(String name) {
}
