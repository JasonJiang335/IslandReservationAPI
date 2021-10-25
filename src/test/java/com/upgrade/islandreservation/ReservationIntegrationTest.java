package com.upgrade.islandreservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.json.JSONException;
import org.json.JSONObject;
import static org.junit.Assert.*;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrade.islandreservation.IslandreservationApplication;
import com.upgrade.islandreservation.model.Reservation;
import com.upgrade.islandreservation.model.ReservationRepository;
import com.upgrade.islandreservation.model.mapper.ReservationMapper;
import com.upgrade.islandreservation.service.ReservationService;
import com.upgrade.islandreservation.model.ReservationDate;
import com.upgrade.islandreservation.model.ReservationDateRepository;
import com.upgrade.islandreservation.model.dto.ReservationDTO;
import com.upgrade.islandreservation.model.dto.ReservationDateDTO;
import com.upgrade.islandreservation.model.mapper.ReservationDateMapper;
import com.upgrade.islandreservation.service.ReservationDateService;
import com.upgrade.islandreservation.controller.ReservationDateController;
import com.upgrade.islandreservation.controller.ReservationController;

/**
 * Integration Test for Reservation REST controller.
 *
 * @see ReservationController
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = IslandreservationApplication.class)
public class ReservationIntegrationTest {
    private static final String EMAIL = "john.doe@example.com";
    private static final String FULLNAME = "John Doe";
    private String uniqueID = "";

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private ReservationDateRepository reservationDateRepository;

    @Autowired
    private ReservationDateService reservationDateService;

    @Autowired
    private ReservationDateMapper reservationDateMapper;

    @Autowired
    private Validator validator;

    private ReservationDate reservationDate;

    private MockMvc reservationMockMvc;

    private Reservation reservation;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ReservationController reservationController =
                new ReservationController(reservationService, reservationMapper);
        this.reservationMockMvc = MockMvcBuilders.standaloneSetup(reservationController)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setValidator(validator).build();
    }

    public static Reservation createEntity() {
        Reservation reservation = new Reservation();
        reservation.setEmail(EMAIL);
        reservation.setFullName(FULLNAME);
        reservation.setArrivalDate(LocalDate.now().plusDays(3));
        reservation.setDepartureDate(LocalDate.now().plusDays(6));
        return reservation;
    }

    @Before
    public void initTest() {
        reservation = createEntity();
    }

    @Test
    @Transactional
    public void createReservationSuccess() throws Exception {
        ReservationDate reservationDatesArrival = ReservationDateIntegrationTest.createEntity(); // 3 days from now
        ReservationDate reservationDatesDeparture = ReservationDateIntegrationTest.createEntity();
        reservationDatesDeparture.setDate(LocalDate.now().plusDays(6));
        reservationDatesDeparture = reservationDateRepository.saveAndFlush(reservationDatesDeparture);
        int reservationRepoSizeBefore = reservationRepository.findAll().size();

        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);
        MvcResult result = reservationMockMvc.perform(post("/api/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(reservationDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        try {
            JSONObject response = new JSONObject(result.getResponse().getContentAsString());
            uniqueID = response.getString("uniqueID");
        } catch (JSONException e) {
            System.err.println("Error parsing response JSON String");
        }

        int reservationRepoSizeAfter = reservationRepository.findAll().size();
        assertEquals((long) reservationRepoSizeAfter, (long) reservationRepoSizeBefore + 1);

        assertFalse(reservationDateRepository.getOne(reservationDatesArrival.getId()).getAvailability());
        assertFalse(reservationDateRepository.getOne(reservationDatesDeparture.getId()).getAvailability());
    }

    @Test
    @Transactional
    public void createReservationWithoutAvailableDates() throws Exception {
        ReservationDate reservationDatesArrival = ReservationDateIntegrationTest.createEntity(); // 3 days from now
        ReservationDate reservationDatesDeparture = ReservationDateIntegrationTest.createEntity();
        reservationDatesDeparture.setDate(LocalDate.now().plusDays(4));
        reservationDatesDeparture = reservationDateRepository.saveAndFlush(reservationDatesDeparture);
        int reservationRepoSizeBefore = reservationRepository.findAll().size();

        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);
        reservationMockMvc.perform(post("/api/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(reservationDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void createReservationWithMoreThanThreeDays() throws Exception {
        ReservationDate reservationDatesArrival = ReservationDateIntegrationTest.createEntity();
        reservationDatesArrival.setDate(LocalDate.now().plusDays(7));
        reservationDatesArrival = reservationDateRepository.saveAndFlush(reservationDatesArrival);
        ReservationDate reservationDatesDeparture = ReservationDateIntegrationTest.createEntity();
        reservationDatesDeparture.setDate(LocalDate.now().plusDays(11));
        reservationDatesDeparture = reservationDateRepository.saveAndFlush(reservationDatesDeparture);
        int reservationRepoSizeBefore = reservationRepository.findAll().size();

        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);
        reservationMockMvc.perform(post("/api/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(reservationDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void createReservationWithInvalidArrivalDate() throws Exception {
        ReservationDate reservationDatesArrival = ReservationDateIntegrationTest.createEntity();
        reservationDatesArrival.setDate(LocalDate.now().minusDays(2));
        reservationDatesArrival = reservationDateRepository.saveAndFlush(reservationDatesArrival);
        ReservationDate reservationDatesDeparture = ReservationDateIntegrationTest.createEntity();
        reservationDatesDeparture.setDate(LocalDate.now().plusDays(2));
        reservationDatesDeparture = reservationDateRepository.saveAndFlush(reservationDatesDeparture);
        int reservationRepoSizeBefore = reservationRepository.findAll().size();

        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);
        reservationMockMvc.perform(post("/api/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(reservationDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void createReservationWithInvalidDepartureDate() throws Exception {
        ReservationDate reservationDatesArrival = ReservationDateIntegrationTest.createEntity();
        reservationDatesArrival.setDate(LocalDate.now().plusDays(2));
        reservationDatesArrival = reservationDateRepository.saveAndFlush(reservationDatesArrival);
        ReservationDate reservationDatesDeparture = ReservationDateIntegrationTest.createEntity();
        reservationDatesDeparture.setDate(LocalDate.now().plusDays(1));
        reservationDatesDeparture = reservationDateRepository.saveAndFlush(reservationDatesDeparture);
        int reservationRepoSizeBefore = reservationRepository.findAll().size();

        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);
        reservationMockMvc.perform(post("/api/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(reservationDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void updateReservationSuccess() throws Exception {
        if (!uniqueID.isEmpty()) {
            reservation.setArrivalDate(LocalDate.now().plusDays(4));
            reservation.setDepartureDate(LocalDate.now().plusDays(7));
            ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

            reservationMockMvc.perform(put("/api/reservation/{uniqueID}", uniqueID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(reservationDTO)))
                    .andExpect(status().isOk());

            List<Reservation> reservationList = reservationRepository.findAll();
            Reservation updatedReservation = reservationList.get(reservationList.size() - 1);
            assertThat(updatedReservation.getArrivalDate()).isEqualTo(LocalDate.now().plusDays(4));
            assertThat(updatedReservation.getDepartureDate()).isEqualTo(LocalDate.now().plusDays(7));
        } else {
            assertTrue(false);
        }
    }

    @Test
    @Transactional
    public void updateCurrentActiveReservation() throws Exception {
        if (!uniqueID.isEmpty()) {
            reservation.setArrivalDate(LocalDate.now().minusDays(1));
            reservation.setDepartureDate(LocalDate.now().plusDays(1));
            reservationRepository.saveAndFlush(reservation);
            ReservationDTO reservationDTO = new ReservationDTO();
            reservationDTO.setEmail(EMAIL);
            reservationDTO.setFullName(FULLNAME);
            reservationDTO.setArrivalDate(LocalDate.now().plusDays(10));
            reservationDTO.setDepartureDate(LocalDate.now().plusDays(12));

            reservationMockMvc.perform(put("/api/reservation/{uniqueID}", uniqueID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(reservationDTO)))
                    .andExpect(status().isBadRequest());
        } else {
            assertTrue(false);
        }
    }

    @Test
    @Transactional
    public void updateNotExistReservation() throws Exception {
        reservation.setArrivalDate(LocalDate.now().plusDays(7));
        reservation.setDepartureDate(LocalDate.now().plusDays(9));
        ReservationDTO reservationDTO = reservationMapper.toDto(reservation);

        reservationMockMvc.perform(put("/api/reservation/{uniqueID}", "12345")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(reservationDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void cancelCurrentActiveReservation() throws Exception {
        reservation.setArrivalDate(LocalDate.now().minusDays(1));
        reservation.setDepartureDate(LocalDate.now().plusDays(1));
        reservationRepository.saveAndFlush(reservation);

        reservationMockMvc.perform(delete("/api/reservation/{uniqueID}", uniqueID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void cancelReservationSuccess() throws Exception {
        if (!uniqueID.isEmpty()) {
            int reservationRepoSizeBefore = reservationRepository.findAll().size();
            ReservationDate reservationDatesArrival = ReservationDateIntegrationTest.createEntity();
            reservationDatesArrival.setDate(LocalDate.now().plusDays(4));
            reservationDatesArrival = reservationDateRepository.saveAndFlush(reservationDatesArrival);
            ReservationDate reservationDatesDeparture = ReservationDateIntegrationTest.createEntity();
            reservationDatesDeparture.setDate(LocalDate.now().plusDays(7));
            reservationDatesDeparture = reservationDateRepository.saveAndFlush(reservationDatesDeparture);

            reservationMockMvc.perform(delete("/api/reservation/{uniqueID}", uniqueID)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            assertThat(reservationRepository.findByUniqueID(uniqueID)).isEmpty();
            int reservationRepoSizeAfter = reservationRepository.findAll().size();
            assertEquals((long) reservationRepoSizeAfter, (long) reservationRepoSizeBefore -1);

            assertTrue(reservationDateRepository.getOne(reservationDatesArrival.getId()).getAvailability());
            assertTrue(reservationDateRepository.getOne(reservationDatesDeparture.getId()).getAvailability());
        } else {
            assertTrue(false);
        }
    }

    @Test
    @Transactional
    public void cancelNotExistReservation() throws Exception {
        reservationMockMvc.perform(delete("/api/reservation/{uniqueID}", uniqueID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
