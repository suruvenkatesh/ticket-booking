package com.ticketbooking.domain;
import java.util.Objects;
public class Seat {
    private final String row;
    private final int number;

public Seat(String row, int number){
    this.row = row;
    this.number = number;
}

public String getRow(){
    return this.row;
}
public int getNumber(){
    return this.number;
}

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(!(o instanceof Seat)){
            return false;
        }
        Seat other = (Seat) o;
        return this.row.equals(other.row) && this.number == other.number;
    }

    @Override
    public int hashCode() {
       return Objects.hash(row, number);
    }
}
