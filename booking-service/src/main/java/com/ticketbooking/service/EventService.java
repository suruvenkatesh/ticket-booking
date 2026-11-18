package com.ticketbooking.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.ticketbooking.domain.Event;
import com.ticketbooking.domain.Seat;
import com.ticketbooking.domain.Venue;
import com.ticketbooking.repository.EventRepository;

// The service layer: business rules live here.
// Controller (web) → service (rules) → repository (storage).
// @Service makes this a bean, just like @Repository; the name says "business logic".
@Service
public class EventService {

    private final EventRepository eventRepository;

    // A fixed demo venue for now. Venues get their own API in a later step.
    private final Venue cityHall = new Venue("City Hall", Set.of(new Seat("A", 1), new Seat("A", 2)));

    // Constructor injection, as in the controller. Also puts two demo events into storage.
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        eventRepository.save(new Event("Rock Night", cityHall));
        eventRepository.save(new Event("Jazz Evening",
                new Venue("Blue Room", Set.of(new Seat("A", 1)))));
    }

    // The rule for now: every new event goes into City Hall.
    public Event create(String name) {
        return eventRepository.save(new Event(name, cityHall));
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }
}
