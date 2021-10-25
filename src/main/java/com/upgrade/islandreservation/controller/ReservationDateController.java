package com.upgrade.islandreservation.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.upgrade.islandreservation.service.ReservationDateService;
import com.upgrade.islandreservation.model.dto.ReservationDateDTO;
import com.upgrade.islandreservation.model.mapper.ReservationDateMapper;

@RestController
@RequestMapping("/api")
@CrossOrigin(
		origins = "*",
		maxAge = 3600,
		exposedHeaders = {"current-page", "total-count", "total-items"})
public class ReservationDateController {
    private final ReservationDateService reservationDateService;
    private final ReservationDateMapper reservationDateMapper;

    public ReservationDateController(ReservationDateService reservationDateService, ReservationDateMapper reservationDateMapper) {
    	this.reservationDateService = reservationDateService;
    	this.reservationDateMapper = reservationDateMapper;
    }
    
    /**
     * GET  /reservationDates : List available dates
     *
     * @param start the min date
     * @param end the max date
     * @param available if specific date is available
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of available dates as body
	 * status 400 (Bad Request) no reservation dates data populates
     */
	@GetMapping("/reservationDates")
	public ResponseEntity<Object> getReservationDates(
			@RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate start,
			@RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate end,
			Boolean available, Pageable pageable) {
		try {
			Page <ReservationDateDTO> page = reservationDateService
					.listReservationDates(start, end, available, pageable)
					.map(reservationDateMapper::toDto);
			return ResponseEntity.ok().body(page.getContent());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
}
