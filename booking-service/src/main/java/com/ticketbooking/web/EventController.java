package com.ticketbooking.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ticketbooking.domain.Event;
import com.ticketbooking.domain.Seat;
import com.ticketbooking.domain.Venue;
import com.ticketbooking.repository.EventRepository;

// Answers web requests. Whatever a method returns is sent back as JSON.
@RestController
public class EventController {

    private final EventRepository eventRepository;

    // Constructor injection: the controller asks for an EventRepository here,
    // and Spring passes in its one shared EventRepository bean. No "new" needed.
    // Runs once, when Spring creates the controller. Also puts two demo events into storage.
    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        eventRepository.save(new Event("Rock Night",
                new Venue("City Hall", Set.of(new Seat("A", 1), new Seat("A", 2)))));
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
}
