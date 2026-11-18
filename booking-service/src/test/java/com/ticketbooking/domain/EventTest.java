package com.ticketbooking.domain;

import java.util.Set;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EventTest {

    // JUnit creates a new EventTest object for every test, so each test gets a fresh event.
    private final Seat a1 = new Seat("A", 1);
    private final Event event = new Event("Test Event", new Venue("Test Venue", Set.of(a1)));

    @Test
    void newEventStartsWithAllSeatsAvailable() {
        assertThat(event.statusOf(a1)).isEqualTo(SeatStatus.AVAILABLE);
    }

    @Test
    void holdingAvailableSeatMakesItHeld() {
        event.hold(a1);

        assertThat(event.statusOf(a1)).isEqualTo(SeatStatus.HELD);
    }

    @Test
    void seatCannotBeHeldTwice() {
        event.hold(a1);

        assertThatThrownBy(() -> event.hold(a1)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void seatNotInVenueCannotBeHeld() {
        assertThatThrownBy(() -> event.hold(new Seat("Z", 99))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void confirmingHeldSeatMakesItBooked() {
        event.hold(a1);
        event.confirm(a1);

        assertThat(event.statusOf(a1)).isEqualTo(SeatStatus.BOOKED);
    }

    @Test
    void seatMustBeHeldBeforeConfirming() {
        assertThatThrownBy(() -> event.confirm(a1)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void bookedSeatCannotBeHeld() {
        event.hold(a1);
        event.confirm(a1);

        assertThatThrownBy(() -> event.hold(a1)).isInstanceOf(IllegalStateException.class);
    }
}
