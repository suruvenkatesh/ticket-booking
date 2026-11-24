package com.farmonboard.onboarding.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmonboard.onboarding.domain.FarmerApplication;

// Database access for farmer applications. There's no code here on purpose:
// Spring Data sees this interface at startup and builds the class behind it.
// JpaRepository<FarmerApplication, Long> = "an entity of this type, whose id is a Long".
// We get save, findById, findAll, count, deleteById and more for free.
// No @Repository needed: Spring Data finds every interface that extends JpaRepository.
public interface FarmerApplicationRepository extends JpaRepository<FarmerApplication, Long> {
}
