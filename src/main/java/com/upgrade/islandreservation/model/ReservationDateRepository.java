package com.upgrade.islandreservation.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationDateRepository extends JpaRepository<ReservationDate, Long>{
	Optional<ReservationDate> findByDate(LocalDate date);
	Page<ReservationDate> findByDateBetween(
			LocalDate start, LocalDate end, Pageable pageable);
	Page<ReservationDate> findByDateBetweenAndAvailability(
			LocalDate start, LocalDate end, Boolean availability, Pageable pageable);
	@Lock(value = LockModeType.PESSIMISTIC_READ)
	List<ReservationDate> findByDateBetween(LocalDate start, LocalDate end);
}
