package com.yas.bookingservice.controller;


import com.yas.bookingservice.domain.BookingStatus;
import com.yas.bookingservice.dto.*;
import com.yas.bookingservice.mapper.BookingMapper;
import com.yas.bookingservice.model.Booking;
import com.yas.bookingservice.model.SalonReport;
import com.yas.bookingservice.service.BookingService;
import com.yas.bookingservice.service.client.SalonFeignClient;
import com.yas.bookingservice.service.client.ServiceOfferingFeignClient;
import com.yas.bookingservice.service.client.UserFeignClient;
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
    private final UserFeignClient userFeignClient;
    private final SalonFeignClient  salonFeignClient;
    private final ServiceOfferingFeignClient  serviceOfferingFeignClient;

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestParam Long salonId,
                                                 @RequestBody BookingRequest bookingRequest,
                                                 @RequestHeader("Authorization") String token) throws Exception {
        UserDTO userDTO = userFeignClient.getUserProfile(token).getBody();
        if(userDTO==null){
            throw new Exception("User is not found");
        }

        SalonDTO salonDTO = salonFeignClient.getSalonById(salonId).getBody();

        Set<ServiceDTO>  serviceDTOS = serviceOfferingFeignClient.getServiceOfferingByIds(bookingRequest.getServiceIds()).getBody();

        Booking booking = bookingService.createBooking(bookingRequest, userDTO, salonDTO,serviceDTOS);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/customer")
    public ResponseEntity<Set<BookingDTO>> getBookingsByCustomerId(@RequestHeader("Authorization")String token) throws Exception {
        UserDTO userDTO = userFeignClient.getUserProfile(token).getBody();
        if(userDTO==null){
            throw new Exception("User not found");
        }

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
    public ResponseEntity<List<Booking>> getBookingsBySalonId(
            @RequestHeader("Authorization") String token
    ) throws Exception {

        SalonDTO salonDTO=salonFeignClient.getOwnerByOwnerId(token).getBody();
        if(salonDTO==null){
            throw new Exception("Owner not found");
        }
        List<Booking> booking = bookingService.getBookingBySalon(salonDTO.getId());
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
            @RequestHeader("Authorization") String token
    ) throws Exception {
        SalonDTO salonDTO=salonFeignClient.getOwnerByOwnerId(token).getBody();
        if(salonDTO==null){
            throw new Exception("Owner not found");
        }

        SalonReport salonReport=bookingService.getSalonReport(salonDTO.getId());
        return ResponseEntity.ok(salonReport);
    }
}
