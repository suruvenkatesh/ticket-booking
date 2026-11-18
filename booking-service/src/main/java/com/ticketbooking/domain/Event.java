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

    public void hold(Seat seat) {
        changeStatus(seat, SeatStatus.AVAILABLE, SeatStatus.HELD);
    }

    public void confirm(Seat seat) {
        changeStatus(seat, SeatStatus.HELD, SeatStatus.BOOKED);
    }

    // Wrong input (a seat that isn't here) is IllegalArgumentException.
    // Right seat, wrong moment (already held or booked) is IllegalStateException.
    private void changeStatus(Seat seat, SeatStatus from, SeatStatus to) {
        SeatStatus current = seatStatuses.get(seat);
        if (current == null) {
            throw new IllegalArgumentException("Seat " + seat + " is not in this venue");
        }
        if (current != from) {
            throw new IllegalStateException("Seat " + seat + " is " + current + ", expected " + from);
        }
        seatStatuses.put(seat, to);
    }
}
