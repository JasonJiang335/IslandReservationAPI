package com.upgrade.islandreservation.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.upgrade.islandreservation.model.ReservationDate;
import com.upgrade.islandreservation.model.ReservationDateRepository;

@Service
public class ReservationDateService {
	private ReservationDateRepository reservationDateRepository;
	
	public ReservationDateService(ReservationDateRepository reservationDateRepository) {
		this.reservationDateRepository = reservationDateRepository;
	}

	/**
	 * List all reservation dates
	 * 
	 * @param start min date
	 * @param end max date
	 * @param available only show available dates
	 * @param pageable the pagination information
	 * @return the entities that match the search criteria
	 */
	public Page<ReservationDate> listReservationDates(LocalDate start, LocalDate end, Boolean available, Pageable pageable) throws IllegalArgumentException {
		if (start == null || start.isBefore(LocalDate.now().plusDays(1))) {
			start = LocalDate.now().plusDays(1);
		}
		if (end == null || end.isAfter(LocalDate.now().plusMonths(1))) {
			end = LocalDate.now().plusMonths(1);
		}
		if (available == null) {
			available = true;
		}

		return reservationDateRepository.findByDateBetweenAndAvailability(start, end, available, pageable);
	}
	
	/**
	 * By end of everyday, create a new reservationDate in database for a date one month away
	 */
	@Scheduled(cron = "59 59 23 * * *")
	@Transactional(isolation = Isolation.SERIALIZABLE)
	private void createNewAvailability () {
		LocalDate futureDay = LocalDate.now().plusDays(31);
		Optional<ReservationDate> futureDate = reservationDateRepository.findByDate(futureDay);
		if (!futureDate.isPresent()) {
			ReservationDate date = new ReservationDate();
			date.setDate(futureDay);
			reservationDateRepository.save(date);
		}
	}
}
