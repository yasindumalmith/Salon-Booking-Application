package com.yas.bookingservice.controller;


import com.yas.bookingservice.domain.BookingStatus;
import com.yas.bookingservice.dto.*;
import com.yas.bookingservice.mapper.BookingMapper;
import com.yas.bookingservice.metrics.BookingMetrics;
import com.yas.bookingservice.model.Booking;
import com.yas.bookingservice.model.SalonReport;
import com.yas.bookingservice.service.BookingService;
import com.yas.bookingservice.service.client.SalonFeignClient;
import com.yas.bookingservice.service.client.ServiceOfferingFeignClient;
import com.yas.bookingservice.service.client.UserFeignClient;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserFeignClient userFeignClient;
    private final SalonFeignClient salonFeignClient;
    private final ServiceOfferingFeignClient serviceOfferingFeignClient;
    private final BookingMetrics bookingMetrics;

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/bookings
    // Metrics: total requests, response time, error rate
    // External calls: user-service, salon-service, service-offering (timed)
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestParam Long salonId,
                                                 @RequestBody BookingRequest bookingRequest,
                                                 @RequestHeader("Authorization") String token) throws Exception {

        final String ENDPOINT = "POST /api/bookings";
        bookingMetrics.incrementApiRequest(ENDPOINT, "POST");
        Timer.Sample apiTimer = bookingMetrics.startApiTimer();

        try {
            // ── External call: user-service ──────────────────────────────────
            Timer.Sample userCallTimer = bookingMetrics.startExternalCallTimer();
            UserDTO userDTO;
            try {
                userDTO = userFeignClient.getUserProfile(token).getBody();
                bookingMetrics.stopExternalCallTimer(userCallTimer, "user-service", "getUserProfile");
            } catch (Exception ex) {
                bookingMetrics.stopExternalCallTimer(userCallTimer, "user-service", "getUserProfile");
                bookingMetrics.incrementExternalCallFailure("user-service", "getUserProfile");
                throw ex;
            }

            if (userDTO == null) {
                bookingMetrics.incrementApiError(ENDPOINT, "POST", "UserNotFoundException");
                bookingMetrics.stopApiTimer(apiTimer, ENDPOINT, "POST", "4xx");
                throw new Exception("User is not found");
            }

            // ── External call: salon-service ─────────────────────────────────
            Timer.Sample salonCallTimer = bookingMetrics.startExternalCallTimer();
            SalonDTO salonDTO;
            try {
                salonDTO = salonFeignClient.getSalonById(salonId).getBody();
                bookingMetrics.stopExternalCallTimer(salonCallTimer, "salon-service", "getSalonById");
            } catch (Exception ex) {
                bookingMetrics.stopExternalCallTimer(salonCallTimer, "salon-service", "getSalonById");
                bookingMetrics.incrementExternalCallFailure("salon-service", "getSalonById");
                throw ex;
            }

            // ── External call: service-offering-service ──────────────────────
            Timer.Sample offeringCallTimer = bookingMetrics.startExternalCallTimer();
            Set<ServiceDTO> serviceDTOS;
            try {
                serviceDTOS = serviceOfferingFeignClient
                        .getServiceOfferingByIds(bookingRequest.getServiceIds()).getBody();
                bookingMetrics.stopExternalCallTimer(offeringCallTimer, "service-offering", "getServiceOfferingByIds");
            } catch (Exception ex) {
                bookingMetrics.stopExternalCallTimer(offeringCallTimer, "service-offering", "getServiceOfferingByIds");
                bookingMetrics.incrementExternalCallFailure("service-offering", "getServiceOfferingByIds");
                throw ex;
            }

            Booking booking = bookingService.createBooking(bookingRequest, userDTO, salonDTO, serviceDTOS);
            bookingMetrics.stopApiTimer(apiTimer, ENDPOINT, "POST", "2xx");
            return ResponseEntity.ok(booking);

        } catch (Exception ex) {
            bookingMetrics.incrementApiError(ENDPOINT, "POST", ex.getClass().getSimpleName());
            bookingMetrics.stopApiTimer(apiTimer, ENDPOINT, "POST", "5xx");
            throw ex;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/bookings/customer
    // Metrics: total requests, response time, error rate
    // External calls: user-service (timed)
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/customer")
    public ResponseEntity<Set<BookingDTO>> getBookingsByCustomerId(
            @RequestHeader("Authorization") String token) throws Exception {

        final String ENDPOINT = "GET /api/bookings/customer";
        bookingMetrics.incrementApiRequest(ENDPOINT, "GET");
        Timer.Sample apiTimer = bookingMetrics.startApiTimer();

        try {
            // ── External call: user-service ──────────────────────────────────
            Timer.Sample userCallTimer = bookingMetrics.startExternalCallTimer();
            UserDTO userDTO;
            try {
                userDTO = userFeignClient.getUserProfile(token).getBody();
                bookingMetrics.stopExternalCallTimer(userCallTimer, "user-service", "getUserProfile");
            } catch (Exception ex) {
                bookingMetrics.stopExternalCallTimer(userCallTimer, "user-service", "getUserProfile");
                bookingMetrics.incrementExternalCallFailure("user-service", "getUserProfile");
                throw ex;
            }

            if (userDTO == null) {
                bookingMetrics.incrementApiError(ENDPOINT, "GET", "UserNotFoundException");
                bookingMetrics.stopApiTimer(apiTimer, ENDPOINT, "GET", "4xx");
                throw new Exception("User not found");
            }

            List<Booking> bookings = bookingService.getBookingsByCustomer(userDTO.getId());
            bookingMetrics.stopApiTimer(apiTimer, ENDPOINT, "GET", "2xx");
            return ResponseEntity.ok(getBookingDTOs(bookings));

        } catch (Exception ex) {
            bookingMetrics.incrementApiError(ENDPOINT, "GET", ex.getClass().getSimpleName());
            bookingMetrics.stopApiTimer(apiTimer, ENDPOINT, "GET", "5xx");
            throw ex;
        }
    }

    private Set<BookingDTO> getBookingDTOs(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDTO)
                .collect(Collectors.toSet());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/bookings/salon
    // Metrics: total requests, response time, error rate
    // External calls: salon-service (timed)
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/salon")
    public ResponseEntity<List<Booking>> getBookingsBySalonId(
            @RequestHeader("Authorization") String token) throws Exception {

        final String ENDPOINT = "GET /api/bookings/salon";
        bookingMetrics.incrementApiRequest(ENDPOINT, "GET");
        Timer.Sample apiTimer = bookingMetrics.startApiTimer();

        try {
            // ── External call: salon-service ─────────────────────────────────
            Timer.Sample salonCallTimer = bookingMetrics.startExternalCallTimer();
            SalonDTO salonDTO;
            try {
                salonDTO = salonFeignClient.getOwnerByOwnerId(token).getBody();
                bookingMetrics.stopExternalCallTimer(salonCallTimer, "salon-service", "getOwnerByOwnerId");
            } catch (Exception ex) {
                bookingMetrics.stopExternalCallTimer(salonCallTimer, "salon-service", "getOwnerByOwnerId");
                bookingMetrics.incrementExternalCallFailure("salon-service", "getOwnerByOwnerId");
                throw ex;
            }

            if (salonDTO == null) {
                bookingMetrics.incrementApiError(ENDPOINT, "GET", "OwnerNotFoundException");
                bookingMetrics.stopApiTimer(apiTimer, ENDPOINT, "GET", "4xx");
                throw new Exception("Owner not found");
            }

            List<Booking> bookingList = bookingService.getBookingBySalon(salonDTO.getId());
            bookingMetrics.stopApiTimer(apiTimer, ENDPOINT, "GET", "2xx");
            return ResponseEntity.ok(bookingList);

        } catch (Exception ex) {
            bookingMetrics.incrementApiError(ENDPOINT, "GET", ex.getClass().getSimpleName());
            bookingMetrics.stopApiTimer(apiTimer, ENDPOINT, "GET", "5xx");
            throw ex;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/bookings/{bookingId}
    // Metrics: total requests, response time, error rate
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDTO> getBookingById(
            @PathVariable("bookingId") Long bookingId) throws Exception {

        final String ENDPOINT = "GET /api/bookings/{bookingId}";
        bookingMetrics.incrementApiRequest(ENDPOINT, "GET");
        Timer.Sample apiTimer = bookingMetrics.startApiTimer();

        try {
            Booking booking = bookingService.getBookingById(bookingId);
            bookingMetrics.stopApiTimer(apiTimer, ENDPOINT, "GET", "2xx");
            return ResponseEntity.ok(BookingMapper.toBookingDTO(booking));
        } catch (Exception ex) {
            bookingMetrics.incrementApiError(ENDPOINT, "GET", ex.getClass().getSimpleName());
            bookingMetrics.stopApiTimer(apiTimer, ENDPOINT, "GET", "5xx");
            throw ex;
        }
    }

    @PutMapping("/{bookingId}/status")
    public ResponseEntity<BookingDTO> updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestParam BookingStatus bookingStatus) throws Exception {
        Booking booking = bookingService.updateBooking(bookingId, bookingStatus);
        return ResponseEntity.ok(BookingMapper.toBookingDTO(booking));
    }

    @GetMapping("/slots/salon/{salonId}/date/{date}")
    public ResponseEntity<List<SlotDTO>> getBookingBySalonAndDate(
            @PathVariable Long salonId,
            @RequestParam LocalDate date) {
        List<Booking> bookings = bookingService.getBookingByDate(date, salonId);
        List<SlotDTO> slotDTOs = bookings.stream()
                .map(booking -> {
                    SlotDTO slotDTO = new SlotDTO();
                    slotDTO.setStartTime(booking.getStartTime());
                    slotDTO.setEndTime(booking.getEndTime());
                    return slotDTO;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(slotDTOs);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/bookings/report
    // Metrics: total requests, response time, error rate
    // External calls: salon-service (timed)
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/report")
    public ResponseEntity<SalonReport> getSalonReport(
            @RequestHeader("Authorization") String token) throws Exception {

        final String ENDPOINT = "GET /api/bookings/report";
        bookingMetrics.incrementApiRequest(ENDPOINT, "GET");
        Timer.Sample apiTimer = bookingMetrics.startApiTimer();

        try {
            // ── External call: salon-service ─────────────────────────────────
            Timer.Sample salonCallTimer = bookingMetrics.startExternalCallTimer();
            SalonDTO salonDTO;
            try {
                salonDTO = salonFeignClient.getOwnerByOwnerId(token).getBody();
                bookingMetrics.stopExternalCallTimer(salonCallTimer, "salon-service", "getOwnerByOwnerId");
            } catch (Exception ex) {
                bookingMetrics.stopExternalCallTimer(salonCallTimer, "salon-service", "getOwnerByOwnerId");
                bookingMetrics.incrementExternalCallFailure("salon-service", "getOwnerByOwnerId");
                throw ex;
            }

            if (salonDTO == null) {
                bookingMetrics.incrementApiError(ENDPOINT, "GET", "OwnerNotFoundException");
                bookingMetrics.stopApiTimer(apiTimer, ENDPOINT, "GET", "4xx");
                throw new Exception("Owner not found");
            }

            SalonReport salonReport = bookingService.getSalonReport(salonDTO.getId());
            bookingMetrics.stopApiTimer(apiTimer, ENDPOINT, "GET", "2xx");
            return ResponseEntity.ok(salonReport);

        } catch (Exception ex) {
            bookingMetrics.incrementApiError(ENDPOINT, "GET", ex.getClass().getSimpleName());
            bookingMetrics.stopApiTimer(apiTimer, ENDPOINT, "GET", "5xx");
            throw ex;
        }
    }
}
