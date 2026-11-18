package com.ticketbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Marks the starting point of the app. Spring looks for its classes in this package
// (com.ticketbooking) and every package under it.
@SpringBootApplication
public class BookingServiceApplication {

    // Starts Spring, and Spring starts the Tomcat web server.
    public static void main(String[] args) {
        SpringApplication.run(BookingServiceApplication.class, args);
    }
}
