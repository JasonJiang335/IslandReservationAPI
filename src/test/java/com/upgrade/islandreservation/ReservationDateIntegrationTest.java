package com.upgrade.islandreservation;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrade.islandreservation.IslandreservationApplication;
import com.upgrade.islandreservation.model.ReservationDate;
import com.upgrade.islandreservation.model.ReservationDateRepository;
import com.upgrade.islandreservation.model.dto.ReservationDateDTO;
import com.upgrade.islandreservation.model.mapper.ReservationDateMapper;
import com.upgrade.islandreservation.service.ReservationDateService;
import com.upgrade.islandreservation.controller.ReservationDateController;

/**
 * Integration Test for ReservationDate REST controller.
 *
 * @see ReservationDateController
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = IslandreservationApplication.class)
public class ReservationDateIntegrationTest {
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private ReservationDateRepository reservationDateRepository;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ReservationDateService reservationDateService;

    @Autowired
    private ReservationDateMapper reservationDateMapper;

    @Autowired
    private Validator validator;

    private MockMvc reservationDateMockMvc;

    private ReservationDate reservationDate;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ReservationDateController reservationDateController =
                new ReservationDateController(reservationDateService, reservationDateMapper);
        this.reservationDateMockMvc = MockMvcBuilders.standaloneSetup(reservationDateController)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setValidator(validator).build();
    }

    public static ReservationDate createEntity() {
        ReservationDate reservationDate = new ReservationDate();
        reservationDate.setDate(LocalDate.now().plusDays(3));
        reservationDate.setAvailability(true);
        return reservationDate;
    }

    @Before
    public void initTest() {
        reservationDate = createEntity();
    }

    @Test
    @Transactional
    public void getAvailableReservationDates() throws Exception {
        reservationDateRepository.saveAndFlush(reservationDate);
        reservationDateMockMvc.perform(get("/api/reservationDates"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].availability").value(true));
    }
}
