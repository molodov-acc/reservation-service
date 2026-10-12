package com.molodov.reservationsystem.reservation;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
/// Spring регистрирует класс как контроллер.
@RequestMapping("/reservation")
/// задаёт общий путь для его endpoints.
public class ReservationController {
    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservation() {
        log.info("called getAll reservation");
        List<Reservation> allReservations = reservationService.getAllReservation();

        return ResponseEntity.ok(allReservations);
    }

    @GetMapping("filter")
    public ResponseEntity<List<Reservation>> searchByFilter(
            @RequestParam(name = "roomId", required = false) Long roomId,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber
    ) {
        var filter = new ReservationSearchFilter(roomId, userId, pageSize, pageNumber);
        log.info("called getAll reservation");
        List<Reservation> reservations = reservationService.searchByFilter(filter);

        return ResponseEntity.ok(reservations);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(
            @PathVariable("id") Long id
    ) {
        log.info("called getAll reservation");
        Reservation reservation = reservationService.getById(id);

        return ResponseEntity.ok(reservation);
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(
            /**
             * @Valid запускает проверку аннотаций на объекте запроса.
             * Если проверка не пройдёт, Spring MVC обычно вернёт HTTP 400 Bad Request.
             *
             * @RequestBody — это аннотация Spring, которая говорит:
             * возьми тело HTTP-запроса и преобразуй его в Java-объект.
             * */
            @Valid @RequestBody Reservation reservationToCreate
    ) {
        log.info("called createReservation");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reservationService.createReservation(reservationToCreate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable("id") Long id,
            @Valid @RequestBody Reservation reservationToUpdate
    ) {
        log.info("called getAll updateReservation");

        var update = reservationService.reservationUpdate(id, reservationToUpdate);
        return ResponseEntity.ok(update);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable("id") Long id) {

        log.info("called delete reservation" + id);
        reservationService.cancelReservation(id);
        return ResponseEntity.ok().build();
    }
}
