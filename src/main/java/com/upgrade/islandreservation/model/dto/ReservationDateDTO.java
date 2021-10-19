package com.upgrade.islandreservation.model.dto;

import java.time.LocalDate;

/**
 * DTO for the ReservationDate entity
 */
public class ReservationDateDTO {
	private LocalDate date;
	private Boolean availability;

	public Boolean getAvailability() { return availability; }
	public void setAvailability(Boolean availability) {
		this.availability = availability;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
}
