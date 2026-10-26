package com.ticketbooking.domain;

public record Seat(String row, int number) {

    public Seat {
    if (row ==null || row.isBlank()){
        throw new IllegalArgumentException("Row cannot be null or empty");
    }
    if(number <= 0){
        throw new IllegalArgumentException("Number must be greater than 0");
    }
    }
}
