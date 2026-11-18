package com.ticketbooking.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ticketbooking.domain.Event;
import com.ticketbooking.domain.Seat;
import com.ticketbooking.domain.Venue;

// Answers web requests. Whatever a method returns is sent back as JSON.
@RestController
public class EventController {

    // Created once when the controller is created, so the ids stay the same between requests.
    // Hard-coded for now; a later step moves events into storage.
    private final List<Event> events = List.of(
            new Event("Rock Night", new Venue("City Hall", Set.of(new Seat("A", 1), new Seat("A", 2)))),
            new Event("Jazz Evening", new Venue("Blue Room", Set.of(new Seat("A", 1)))));

    // GET http://localhost:8080/api/events calls this method.
    @GetMapping("/api/events")
    public List<EventResponse> findAll() {
        List<EventResponse> responses = new ArrayList<>();
        for (Event event : events) {
            responses.add(EventResponse.from(event));
        }
        return responses;
    }
}
