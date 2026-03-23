package com.yas.bookingservice.service;

import com.yas.bookingservice.dto.BookingRequest;
import com.yas.bookingservice.dto.SalonDTO;
import com.yas.bookingservice.dto.ServiceDTO;
import com.yas.bookingservice.dto.UserDTO;
import com.yas.bookingservice.model.Booking;
import com.yas.bookingservice.model.SalonReport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface BookingService {
    Booking createBooking(BookingRequest booking, UserDTO user, SalonDTO salon, Set<ServiceDTO> services) throws Exception;
    List<Booking> getBookingsByCustomer(Long customerId);
    Booking getBookingById(Long id);
    Booking updateBooking(Long bookingId, BookingRequest booking);
    List<Booking> getBookingByDate(LocalDateTime date, Long salonId);
    SalonReport getSalonReport(Long salonId);
    List<Booking> getBookingBySalon(Long salonId);
}
