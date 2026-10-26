package com.ticketbooking.domain;

import java.util.Set;

public record Venue(String name, Set<Seat> seats) {

    public Venue {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Venue name cannot be blank");
        }

        seats = Set.copyOf(seats);
    }
}
