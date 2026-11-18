package com.ticketbooking.web;

// A DTO (data transfer object): shaped exactly like the JSON we send back.
// Jackson turns each field into a JSON key: {"name": "...", "venueName": "..."}
public record EventResponse(String name, String venueName) {
}
