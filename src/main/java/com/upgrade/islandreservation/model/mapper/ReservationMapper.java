package com.upgrade.islandreservation.model.mapper;

import org.mapstruct.Mapper;

import com.upgrade.islandreservation.model.Reservation;
import com.upgrade.islandreservation.model.dto.ReservationDTO;

@Mapper(componentModel = "spring", uses = {})
public interface ReservationMapper extends EntityMapper<ReservationDTO, Reservation> {
    default Reservation fromUniqueId(String uuid) {
        Reservation reservation = new Reservation();
        if (uuid == null) {
            reservation.setUniqueID(null);
        } else {
            reservation.setUniqueID(uuid);
        }
        return reservation;
    }
}
