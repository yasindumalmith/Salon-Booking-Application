package com.yas.bookingservice.controller;


import com.yas.bookingservice.domain.BookingStatus;
import com.yas.bookingservice.dto.*;
import com.yas.bookingservice.mapper.BookingMapper;
import com.yas.bookingservice.model.Booking;
import com.yas.bookingservice.model.SalonReport;
import com.yas.bookingservice.service.BookingService;
import jakarta.persistence.SecondaryTable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestParam Long salonId, @RequestBody BookingRequest bookingRequest) throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);

        SalonDTO salonDTO = new SalonDTO();
        salonDTO.setId(salonId);

        salonDTO.setOpeningTime(LocalTime.of(8,0));
        salonDTO.setClosingTime(LocalTime.of(22,0));

        Set<ServiceDTO>  serviceDTOS = new HashSet<>();
        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setId(1L);
        serviceDTO.setPrice(400);
        serviceDTO.setDuration(45);
        serviceDTO.setName("Hair cut");

        serviceDTOS.add(serviceDTO);

        Booking booking = bookingService.createBooking(bookingRequest, userDTO, salonDTO,serviceDTOS);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/customer")
    public ResponseEntity<Set<BookingDTO>> getBookingsByCustomerId() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);

        List<Booking> booking = bookingService.getBookingsByCustomer(userDTO.getId());
        return ResponseEntity.ok(getBookingDTOs(booking));
    }

    private Set<BookingDTO> getBookingDTOs(List<Booking> bookings) {
        return bookings.stream()
                .map(booking -> {
                    return BookingMapper.toBookingDTO(booking);
                }).collect(Collectors.toSet());
    }


    @GetMapping("/salon")
    public ResponseEntity<List<Booking>> getBookingsBySalonId() throws Exception {


        List<Booking> booking = bookingService.getBookingBySalon(1L);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable("bookingId") Long bookingId) throws Exception {
        Booking booking = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(BookingMapper.toBookingDTO(booking));
    }

    @PutMapping("/{bookingId}/status")
    public ResponseEntity<BookingDTO> updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestParam BookingStatus bookingStatus
    ) throws Exception {
        Booking booking = bookingService.updateBooking(bookingId,bookingStatus);
        return ResponseEntity.ok(BookingMapper.toBookingDTO(booking));
    }


    @GetMapping("/slots/salon/{salonId}/date/{date}")
    public ResponseEntity<List<SlotDTO>> getBookingBySalonAndDate(
            @PathVariable Long salonId,
            @RequestParam LocalDate date
    ){
        List<Booking> bookings=bookingService.getBookingByDate(date, salonId);
        List<SlotDTO> slotDTOs= bookings.stream()
                .map(booking -> {
                    SlotDTO slotDTO = new SlotDTO();
                    slotDTO.setStartTime(booking.getStartTime());
                    slotDTO.setEndTime(booking.getEndTime());
                    return slotDTO;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(slotDTOs);
    }

    @GetMapping("/report")
    public ResponseEntity<SalonReport> getSalonReport(

    ){
        SalonReport salonReport=bookingService.getSalonReport(1L);
        return ResponseEntity.ok(salonReport);
    }
}
