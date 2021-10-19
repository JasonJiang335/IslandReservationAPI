package com.upgrade.islandreservation.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String>{
    Optional<Reservation> findByUniqueID (String uniqueID);
    Optional<Reservation> deleteByUniqueID (String uniqueID);
}
