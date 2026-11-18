package com.ticketbooking.repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.ticketbooking.domain.Event;

// The repository pattern: this class's only job is storing and finding events.
// In memory for now; a real database replaces it in stage 3.
public class EventRepository {

    // Each request runs on its own thread, and a ConcurrentHashMap stays safe
    // when many threads use it at the same moment. The key is the event's id.
    private final Map<UUID, Event> events = new ConcurrentHashMap<>();

    public Event save(Event event) {
        events.put(event.getId(), event);
        return event;
    }

    // A locked copy, so callers can't add or remove events behind the repository's back.
    public List<Event> findAll() {
        return List.copyOf(events.values());
    }
}
