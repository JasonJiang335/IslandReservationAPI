package com.upgrade.islandreservation.model;

import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "reservation")
public class Reservation {

	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	private String email;

	@NotNull
	private String fullName;

	@NotNull
	private LocalDate arrivalDate;

	@NotNull
	private LocalDate departureDate;

	@Id
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
	public LocalDate getArrivalDate() {
		return arrivalDate;
	}
	public void setArrivalDate(LocalDate arrivalDate) {
		this.arrivalDate = arrivalDate;
	}
	public LocalDate getDepartureDate() {
		return departureDate;
	}
	public void setDepartureDate(LocalDate departureDate) {
		this.departureDate = departureDate;
	}
	public String getUniqueID() { return this.uniqueID; }

	public void setUniqueID(String uuid) {
		if (uuid != null)
			this.uniqueID = uuid;
		else
			this.uniqueID = UUID.randomUUID().toString();
	}

	@Override
	public String toString() {
		return "Booking{"
				+ "id: " + getId()
				+ ", email: " + getEmail()
				+ ", fullName: " + getFullName()
				+ ", arrivalDate: " + getArrivalDate()
				+ ", departureDate: " + getDepartureDate()
				+ ", UUID: " + getUniqueID() + "}";
	}
}
