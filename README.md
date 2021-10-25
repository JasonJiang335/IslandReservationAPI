# Pacific Island Reservation API
This project provides backend API serivce for Pacific Island reservation.

## Assumption
 * Island can be reserved for only one party in each day
 * Reservation can start in 1-30 days
 * One reservation can be max 3 days
 * No Email validation
 * No authentication
 * Active reservation cannot be changed

## Considerations
 * Reservation dates for next 30 days are populated into database upon start
 * Concurrency is handled by adding trasactional isolation to CRUD methods in both services

## Execution
Run command : `./mvnw spring-boot:run`

Access Address : `localhost:8080/api`

## API Documentation
Link to API documentation created using Swagger:

 * https://app.swaggerhub.com/apis/islandreservation/reservationDates/1.0.0#/

 * https://app.swaggerhub.com/apis/islandreservation/reservations/1.0.0

## Testing

### Integration Test
 * ReservateDate Controller Integration Test : `src\test\java\com.upgrade.islandreservation\ReservationDateIntegrationTest`
 * Reservate Controller Integration Test : `src\test\java\com.upgrade.islandreservation\ReservationIntegrationTest`

### Concurrency Test
