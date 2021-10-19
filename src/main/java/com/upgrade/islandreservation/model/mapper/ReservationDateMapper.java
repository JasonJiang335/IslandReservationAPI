package com.upgrade.islandreservation.model.mapper;

import com.upgrade.islandreservation.model.ReservationDate;
import com.upgrade.islandreservation.model.dto.ReservationDateDTO;

import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {})
public interface ReservationDateMapper extends EntityMapper<ReservationDateDTO, ReservationDate> {
    default ReservationDate fromId(Long id) {
        if (id == null) {
            return null;
        }
        ReservationDate date = new ReservationDate();
        date.setId(id);
        return date;
    }
}
