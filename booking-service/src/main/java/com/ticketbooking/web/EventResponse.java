package com.ticketbooking.web;

import java.util.UUID;

import com.ticketbooking.domain.Event;

// A DTO (data transfer object): shaped exactly like the JSON we send back.
// Jackson turns each field into a JSON key: {"id": "...", "name": "...", "venueName": "..."}
public record EventResponse(UUID id, String name, String venueName) {

    // Copies only what the client needs from an Event. All "Event → JSON" code lives here.
    public static EventResponse from(Event event) {
        return new EventResponse(event.getId(), event.getName(), event.getVenue().name());
    }
}
