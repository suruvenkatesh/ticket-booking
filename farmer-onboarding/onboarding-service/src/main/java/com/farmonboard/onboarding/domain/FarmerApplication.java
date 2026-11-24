package com.farmonboard.onboarding.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

// One farmer onboarding application: one row in the farmer_application table.
@Entity
@Table(name = "farmer_application")
public class FarmerApplication {

    // The primary key. We create it in Java, so the id exists before the row is saved.
    @Id
    private UUID id;

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

    // Every new application starts as a DRAFT, with a new id and the current time.
    public FarmerApplication(String farmerName, String mobileNumber) {
        this.id = UUID.randomUUID();
        this.farmerName = farmerName;
        this.mobileNumber = mobileNumber;
        this.status = ApplicationStatus.DRAFT;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
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
