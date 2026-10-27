package com.ticketbooking.domain;

import java.util.HashMap;
import java.util.Map;

public class Event {
    private final String name;
    private final Venue venue;
    private final Map<Seat, SeatStatus> seatStatuses = new HashMap<>();

    public Event(String name, Venue venue) {
        this.name = name;
        this.venue = venue;
        for (Seat seat : venue.seats()) {
            seatStatuses.put(seat, SeatStatus.AVAILABLE);
        }
    }

    public SeatStatus statusOf(Seat seat) {
        return seatStatuses.get(seat);
    }
}
