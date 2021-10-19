package com.upgrade.islandreservation.model.dto;

import java.time.LocalDate;
import java.util.UUID;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.persistence.Column;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

/**
 * DTO for the Reservation entity
 */
public class ReservationDTO {
	private Long id;
	
	@NotNull
	private String email;
	
	@NotNull
	private String fullName;
	
	@NotNull
	private LocalDate arrivalDate;
	
	@NotNull
	private LocalDate departureDate;

	private String uniqueID;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getArrivalDate() {
		return arrivalDate.toString();
	}
	public LocalDate getArrivalDateAsLocalDate() {
		return arrivalDate;
	}
	public void setArrivalDate(LocalDate arrivalDate) {
		this.arrivalDate = arrivalDate;
	}
	public LocalDate getDepartureDateAsLocalDate() {
		return departureDate;
	}
	public String getDepartureDate() {
		return departureDate.toString();
	}
	public void setDepartureDate(LocalDate departureDate) {
		this.departureDate = departureDate;
	}
	public String getUniqueID() { return this.uniqueID; }

	public void setUniqueID(String uniqueID) {
		if (uniqueID == null)
			this.uniqueID = UUID.randomUUID().toString();
		else
			this.uniqueID = uniqueID;
	}

	@Override
	public String toString() {
		return "BookingDTO{"
				+ "id: " + getId()
				+ ", email: " + getEmail()
				+ ", fullName: " + getFullName()
				+ ", arrivalDate: " + getArrivalDate()
				+ ", departureDate: " + getDepartureDateAsLocalDate()
				+ ", UUID: " + getUniqueID() + "}";
	}
}
