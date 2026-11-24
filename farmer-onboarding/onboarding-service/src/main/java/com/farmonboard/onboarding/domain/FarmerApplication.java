package com.farmonboard.onboarding.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

// One farmer onboarding application: one row in the farmer_application table.
@Entity
@Table(name = "farmer_application")
public class FarmerApplication {

    // The primary key: a number PostgreSQL counts up (1, 2, 3...).
    // It's null until the row is saved; then Hibernate reads back the number the database chose.
    // Long, not long: null means "not saved yet". A long would start at 0, which looks like a real id.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Field names become column names: farmerName -> farmer_name.
    private String farmerName;

    private String mobileNumber;

    // Stored as text, like 'DRAFT'. Without this, JPA would store a number (0, 1, 2...).
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    // An exact moment in time; matches the TIMESTAMP WITH TIME ZONE column.
    private Instant createdAt;

    // JPA needs a constructor with no arguments: when it loads a row,
    // it creates an empty object with this and then fills in the fields.
    // protected: JPA can use it, but our own code can't make an empty application.
    protected FarmerApplication() {
    }

    // Every new application starts as a DRAFT, created now.
    // No id here: the database gives it one when the row is saved.
    public FarmerApplication(String farmerName, String mobileNumber) {
        this.farmerName = farmerName;
        this.mobileNumber = mobileNumber;
        this.status = ApplicationStatus.DRAFT;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
