package com.yas.bookingservice.service;

import com.yas.bookingservice.domain.BookingStatus;
import com.yas.bookingservice.dto.BookingRequest;
import com.yas.bookingservice.dto.SalonDTO;
import com.yas.bookingservice.dto.ServiceDTO;
import com.yas.bookingservice.dto.UserDTO;
import com.yas.bookingservice.model.Booking;
import com.yas.bookingservice.model.SalonReport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface BookingService {
    Booking createBooking(BookingRequest booking, UserDTO user, SalonDTO salon, Set<ServiceDTO> services) throws Exception;
    List<Booking> getBookingsByCustomer(Long customerId);
    Booking getBookingById(Long id) throws Exception;
    Booking updateBooking(Long bookingId, BookingStatus status) throws Exception;
    List<Booking> getBookingByDate(LocalDate date, Long salonId);
    SalonReport getSalonReport(Long salonId);
    List<Booking> getBookingBySalon(Long salonId);
}
