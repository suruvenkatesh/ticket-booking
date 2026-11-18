package com.ticketbooking.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Answers web requests. Whatever a method returns is sent back as JSON.
@RestController
public class EventController {

    // GET http://localhost:8080/api/events calls this method.
    // The names are hard-coded for now; a later step reads real events from storage.
    @GetMapping("/api/events")
    public List<String> findAll() {
        return List.of("Rock Night", "Jazz Evening");
    }
}
