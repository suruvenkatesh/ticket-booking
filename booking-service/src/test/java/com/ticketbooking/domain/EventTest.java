package com.ticketbooking.domain;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class EventTest {

    @Test
    void newEventStartsWithAllSeatsAvailable() {
        Venue venue = new Venue("Test Venue", Set.of(new Seat("A", 1)));
        Event event = new Event("Test Event", venue);

        assertThat(event.statusOf(new Seat("A", 1))).isEqualTo(SeatStatus.AVAILABLE);
    }
}
