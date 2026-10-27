package com.ticketbooking.domain;

import java.util.HashSet;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class EventTest {

    @Test
    void testStatusOf() {
        Venue venue = new Venue("Test Venue", new HashSet<>() {
            {
                add(new Seat("A1", 1));
            }
        });
        Event event = new Event("Test Event", venue);
        // Seat seat = venue.seats()[0];
        assertThat(event.statusOf(new Seat("A1", 1))).isEqualTo(SeatStatus.AVAILABLE);
    }
}
