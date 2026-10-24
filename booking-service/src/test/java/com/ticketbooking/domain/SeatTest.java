package com.ticketbooking.domain;

import java.util.Set;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SeatTest{

@Test
void seatsWithSameRowAndNumberShouldBeEqual(){
    Seat first = new Seat("A",1);
    Seat second =  new Seat("A",1);
    assertThat(first).isEqualTo(second);

}
@Test
void hashSetStoresSameSeatOnlyOnce(){
    Set<Seat> seats = new HashSet<>();
    seats.add(new Seat("A",1));
    seats.add(new Seat("A",1));
    assertThat(seats).hasSize(1);
}   
}
