package com.upgrade.islandreservation.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.upgrade.islandreservation.service.ReservationService;
import com.upgrade.islandreservation.model.dto.ReservationDTO;
import com.upgrade.islandreservation.model.mapper.ReservationMapper;

import javassist.NotFoundException;

@RestController
@RequestMapping("/api")
@CrossOrigin(
		origins = "*",
		maxAge = 3600,
		exposedHeaders = {"current-page", "total-count", "total-items"})
public class ReservationController {
	private ReservationService reservationService;
	private ReservationMapper reservationMapper;
	
	public ReservationController(ReservationService reservationService, ReservationMapper reservationMapper) {
		this.reservationService = reservationService;
		this.reservationMapper = reservationMapper;
	}

    /**
     * POST  /reservation : Create a new reservation.
     *
     * @param reservationDTO the reservationDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new reservationDTO,
     * status 400 (Bad Request) if the reservationDTO is not valid
     */
	@PostMapping("/reservation")
	public ResponseEntity<Object> createReservation(@Valid @RequestBody ReservationDTO reservationDTO) {
		if (reservationDTO.getId() != null) {
			return ResponseEntity.badRequest().body("A new reservation cannot already have an ID");
        }
		try {
			ReservationDTO result = reservationMapper.toDto(reservationService.create(reservationDTO));
			return ResponseEntity.status(HttpStatus.CREATED).body(result);			
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
    /**
     * PUT  /reservation/:uniqueID : Update an existing reservation.
     *
     * @param reservationDTO the reservationDTO to update
	 * @param uniqueID the id of the reservation to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated reservationDTO,
     * status 400 (Bad Request) if the reservationDTO is not valid,
     * status 404 (Not Found) if the resource does not exist,
     * status 500 (Internal Server Error) if the reservationDTO couldn't be updated
     */
	@PutMapping("/reservation/{uniqueID}")
	public ResponseEntity<Object> updateReservation(@PathVariable String uniqueID, @Valid @RequestBody ReservationDTO reservationDTO) {
		try {
			ReservationDTO result = reservationMapper.toDto(reservationService.update(uniqueID, reservationDTO));
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

    /**
     * DELETE  /reservation/:uniqueID : Cancel the "id" reservation.
     *
     * @param uniqueID the id of the reservation to cancel
     * @return the ResponseEntity with status 200 (OK)
     * with status 404 (Not Found) if the resource does not exist,
     * with status 400 (Bad Request) if the request couldn't be completed.
     */
	@DeleteMapping("/reservation/{uniqueID}")
	public ResponseEntity<Object> cancelReservation(@PathVariable String uniqueID) {
		try {
			reservationService.cancel(uniqueID);
			return ResponseEntity.ok().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}	
}