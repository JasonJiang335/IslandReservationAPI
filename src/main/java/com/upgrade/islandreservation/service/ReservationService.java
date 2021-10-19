package com.upgrade.islandreservation.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.upgrade.islandreservation.model.Reservation;
import com.upgrade.islandreservation.model.ReservationDate;
import com.upgrade.islandreservation.model.ReservationDateRepository;
import com.upgrade.islandreservation.model.ReservationRepository;
import com.upgrade.islandreservation.model.dto.ReservationDTO;
import com.upgrade.islandreservation.model.mapper.ReservationMapper;

import javassist.NotFoundException;

@Service
public class ReservationService {
	private ReservationMapper reservationMapper;
	private ReservationDateRepository reservationDateRepository;
	private ReservationRepository reservationRepository;
	
	public ReservationService(ReservationMapper reservationMapper, ReservationDateRepository reservationDateRepository,
							  ReservationRepository reservationRepository) {
		this.reservationMapper = reservationMapper;
		this.reservationDateRepository = reservationDateRepository;
		this.reservationRepository = reservationRepository;
	}

	/**
	 * Create a reservation and update the availability of reservationDates.
	 * 
	 * @param reservationDTO the data of reservation to create
	 * @return the created entity
	 */
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public Reservation create(ReservationDTO reservationDTO) throws IllegalArgumentException{
		Reservation reservation = reservationMapper.toEntity(reservationDTO);
		if (isValidAvailableReservation(reservation)) {
			return reservationRepository.save(reservation);
		} else {
			throw new IllegalArgumentException("Unable to CREATE reservation due to invalid or unavailable reservation dates");
		}
	}

	/**
	 * Update an existing reservation and update reservationDates to reflect updated availabilities
	 *
	 * @param uuid the uniqueID of current reservation
	 * @param reservationDTO
	 * @return the updated reservation
	 */
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public Reservation update(String uuid, ReservationDTO reservationDTO) throws NotFoundException, IllegalArgumentException {
		Optional<Reservation> curReservationOptional = reservationRepository.findByUniqueID(uuid);
		if (curReservationOptional.isPresent()) {
			Reservation curReservation = curReservationOptional.get();
			if (curReservation.getArrivalDate().isBefore(LocalDate.now())
					|| curReservation.getArrivalDate().isEqual(LocalDate.now())) {
				throw new IllegalArgumentException("Cannot modify current ongoing reservation");
			}
			// Set old reservation dates back to available
			LocalDate curStartDate = curReservation.getArrivalDate();
			LocalDate curEndDate = curReservation.getDepartureDate();
			List<ReservationDate> curReservationDatesList = reservationDateRepository.findByDateBetween(curStartDate, curEndDate);
			for (ReservationDate date : curReservationDatesList) {
				date.setAvailability(true);
			}
			reservationDateRepository.saveAll(curReservationDatesList);
			// Update new reservation dates
			Reservation newReservation = reservationMapper.toEntity(reservationDTO);
			if (isValidAvailableReservation(newReservation)) {
				return reservationRepository.save(newReservation);
			} else {
				throw new IllegalArgumentException("Unable to UPDATE reservation due to invalid or unavailable reservation dates");
			}
		} else {
			throw new NotFoundException("Unable to UPDATE reservation due to current reservation cannot be found");
		}
	}
	
	/**
	 * Cancel an existing reservation and update reservationDates to make cancelled dates available
	 * 
	 * @param uuid the uniqueID of current reservation
	 */
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void cancel(String uuid) throws NotFoundException, IllegalArgumentException  {
		Optional<Reservation> curReservationOptional = reservationRepository.findByUniqueID(uuid);
		if (curReservationOptional.isPresent()) {
			Reservation curReservation = curReservationOptional.get();
			if (curReservation.getArrivalDate().isBefore(LocalDate.now())
					|| curReservation.getArrivalDate().isEqual(LocalDate.now())) {
				throw new IllegalArgumentException("Cannot delete current ongoing reservation");
			}
			// Set old reservation dates back to available
			LocalDate curStartDate = curReservation.getArrivalDate();
			LocalDate curEndDate = curReservation.getDepartureDate();
			List<ReservationDate> curReservationDatesList = reservationDateRepository.findByDateBetween(curStartDate, curEndDate);
			for (ReservationDate date : curReservationDatesList) {
				date.setAvailability(true);
			}
			reservationDateRepository.saveAll(curReservationDatesList);
			reservationRepository.deleteByUniqueID(uuid);
		} else {
			throw new NotFoundException("Unable to DELETE reservation due to current reservation cannot be found");
		}
	}

	/**
	 * Validates the current reservation start and end date and check for every day's availability
	 * If valid and available, update the availability upon completing the new reservation
	 *
	 * @param reservation the new reservation object
	 * @return boolean value indicating if everyday in new reservation is valid and available
	 */
	private boolean isValidAvailableReservation (Reservation reservation) {
		LocalDate start = reservation.getArrivalDate();
		LocalDate end = reservation.getDepartureDate();

		if (start.isBefore(LocalDate.now().plusDays(1))
				|| start.isAfter(LocalDate.now().plusDays(29))
				|| end.isBefore(LocalDate.now().plusDays(2))
				|| end.isAfter(LocalDate.now().plusMonths(1))
				|| start.plusDays(3).isBefore(end)
				|| start.plusDays(1).isAfter(end)) {
			return false;
		}
		List<ReservationDate> reservationDatesList = reservationDateRepository.findByDateBetween(start, end);
		for (ReservationDate date : reservationDatesList) {
			if (!date.getAvailability()) {
				return false;
			} else {
				date.setAvailability(false);
			}
		}
		reservationDateRepository.saveAll(reservationDatesList);
		return true;
	}
	
}
