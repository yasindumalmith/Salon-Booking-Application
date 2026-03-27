package com.yas.bookingservice.mapper;

import com.yas.bookingservice.dto.BookingDTO;
import com.yas.bookingservice.model.Booking;

public class BookingMapper {
    public static BookingDTO toBookingDTO(Booking booking) {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(booking.getId());
        bookingDTO.setCustomerId(booking.getCustomerId());
        bookingDTO.setStatus(booking.getStatus());
        bookingDTO.setStartTime(booking.getStartTime());
        bookingDTO.setEndTime(booking.getEndTime());
        bookingDTO.setSalonId(booking.getSalonId());
        bookingDTO.setServiceIds(booking.getServiceIds());
        return bookingDTO;
    }


}
