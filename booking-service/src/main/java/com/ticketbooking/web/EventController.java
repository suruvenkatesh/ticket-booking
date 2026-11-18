package com.ticketbooking.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ticketbooking.domain.Event;
import com.ticketbooking.domain.Seat;
import com.ticketbooking.domain.Venue;
import com.ticketbooking.repository.EventRepository;

import jakarta.validation.Valid;

// Answers web requests. Whatever a method returns is sent back as JSON.
@RestController
public class EventController {

    private final EventRepository eventRepository;

    // A fixed demo venue for now. Venues get their own API in a later step.
    private final Venue cityHall = new Venue("City Hall", Set.of(new Seat("A", 1), new Seat("A", 2)));

    // Constructor injection: the controller asks for an EventRepository here,
    // and Spring passes in its one shared EventRepository bean. No "new" needed.
    // Runs once, when Spring creates the controller. Also puts two demo events into storage.
    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        eventRepository.save(new Event("Rock Night", cityHall));
        eventRepository.save(new Event("Jazz Evening",
                new Venue("Blue Room", Set.of(new Seat("A", 1)))));
    }

    // GET http://localhost:8080/api/events calls this method.
    @GetMapping("/api/events")
    public List<EventResponse> findAll() {
        List<EventResponse> responses = new ArrayList<>();
        for (Event event : eventRepository.findAll()) {
            responses.add(EventResponse.from(event));
        }
        return responses;
    }

    // POST http://localhost:8080/api/events with JSON like {"name": "Comedy Night"}.
    // @RequestBody: Jackson turns the JSON into a CreateEventRequest.
    // @Valid: Spring checks the request's rules (@NotBlank) first. If one fails, it replies 400 and this method never runs.
    // ResponseEntity lets us pick the status code: 201 Created instead of the usual 200 OK.
    @PostMapping("/api/events")
    public ResponseEntity<EventResponse> create(@Valid @RequestBody CreateEventRequest request) {
        Event event = eventRepository.save(new Event(request.name(), cityHall));
        return ResponseEntity.status(HttpStatus.CREATED).body(EventResponse.from(event));
    }
}
