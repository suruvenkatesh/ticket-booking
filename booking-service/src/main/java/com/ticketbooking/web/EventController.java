package com.ticketbooking.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ticketbooking.domain.Event;
import com.ticketbooking.service.EventService;

import jakarta.validation.Valid;

// Answers web requests. Whatever a method returns is sent back as JSON.
// Only web work here (JSON, @Valid, status codes); the rules live in EventService.
@RestController
public class EventController {

    private final EventService eventService;

    // Constructor injection: Spring passes in its one EventService bean.
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // GET http://localhost:8080/api/events calls this method.
    @GetMapping("/api/events")
    public List<EventResponse> findAll() {
        List<EventResponse> responses = new ArrayList<>();
        for (Event event : eventService.findAll()) {
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
        Event event = eventService.create(request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(EventResponse.from(event));
    }
}
