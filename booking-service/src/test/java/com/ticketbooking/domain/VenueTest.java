package com.ticketbooking.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class VenueTest {

    @Test
    void changingOriginalSetDoesNotChangeVenue() {
        Set<Seat> seats = new HashSet<>();
        seats.add(new Seat("A", 1));
        Venue venue = new Venue("Hall 1", seats);
        seats.add(new Seat("A", 2));
        assertThat(venue.seats()).hasSize(1);
    }

    @Test
    void venueNameCannotBeBlank() {
        assertThatThrownBy(() -> new Venue(" ", Set.of(new Seat("A", 1)))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void venueSeatsCannotBeChanged() {
        Set<Seat> seats = new HashSet<>();
        seats.add(new Seat("A", 1));
        Venue venue = new Venue("Hall 1", seats);
        assertThatThrownBy(() -> venue.seats().add(new Seat("A", 2))).isInstanceOf(UnsupportedOperationException.class);
    }

}
