package com.upgrade.islandreservation.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "reservationDate")
public class ReservationDate {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
    @NotNull
    @Column(name = "date", nullable = false, unique = true)
	private LocalDate date;

    @Column(name = "availability")
	private Boolean availability;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public Boolean getAvailability() {
		return this.availability;
	}
	public void setAvailability(Boolean availability) {
		this.availability = availability;
	} 
}
