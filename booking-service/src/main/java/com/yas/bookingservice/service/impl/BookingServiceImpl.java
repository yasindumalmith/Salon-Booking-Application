package com.yas.bookingservice.service.impl;


import com.yas.bookingservice.domain.BookingStatus;
import com.yas.bookingservice.dto.BookingRequest;
import com.yas.bookingservice.dto.SalonDTO;
import com.yas.bookingservice.dto.ServiceDTO;
import com.yas.bookingservice.dto.UserDTO;
import com.yas.bookingservice.model.Booking;
import com.yas.bookingservice.model.SalonReport;
import com.yas.bookingservice.repository.BookingRepository;
import com.yas.bookingservice.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    @Override
    public Booking createBooking(BookingRequest booking, UserDTO user, SalonDTO salon, Set<ServiceDTO> services) throws Exception {
        int totalDuration= services.stream()
                .mapToInt(ServiceDTO::getDuration)
                .sum();

        LocalDateTime bookingStartTime = booking.getStartTime();
        LocalDateTime bookingEndTime = bookingStartTime.plusMinutes(totalDuration);

        Boolean isSlotAvailable= isTimeSlotAvailable(salon,bookingStartTime,bookingEndTime);

        int totalPrice = services.stream()
                .mapToInt(ServiceDTO::getPrice)
                .sum();

        Set<Long> idList = services.stream()
                .map(ServiceDTO::getId)
                .collect(Collectors.toSet());

        Booking bookingEntity = new Booking();
        bookingEntity.setCustomerId(user.getId());
        bookingEntity.setSalonId(salon.getId());
        bookingEntity.setServiceIds(idList);
        bookingEntity.setStatus(BookingStatus.PENDING);
        bookingEntity.setStartTime(bookingStartTime);
        bookingEntity.setEndTime(bookingEndTime);
        bookingEntity.setTotalPrice(totalPrice);
        return bookingRepository.save(bookingEntity);

    }

    public Boolean isTimeSlotAvailable(SalonDTO salonDTO, LocalDateTime bookingStartTime, LocalDateTime bookingEndTime) throws Exception {

        List<Booking> existingBookings = getBookingBySalon(salonDTO.getId());


        LocalDateTime salonOpenTime = salonDTO.getOpeningTime().atDate(bookingStartTime.toLocalDate());
        LocalDateTime salonCloseTime= salonDTO.getClosingTime().atDate(bookingEndTime.toLocalDate());

        if(bookingStartTime.isBefore(salonOpenTime) || bookingEndTime.isAfter(salonCloseTime)){
            throw new Exception("Booking time must within salon Working hours");
        }

        for (Booking existingBooking : existingBookings) {
            LocalDateTime existingBookingStartTime = existingBooking.getStartTime();
            LocalDateTime existingBookingEndTime = existingBooking.getEndTime();

            if(bookingStartTime.isBefore(existingBookingEndTime) && bookingEndTime.isAfter(existingBookingStartTime)){
                throw new Exception("This time slot is already Booked");
            }
            if (bookingStartTime.isEqual(existingBookingStartTime) || bookingEndTime.isEqual(existingBookingEndTime)) {
                throw new Exception("This time slot is already Booked choose different time");
            }
        }


        return true;
    }

    @Override
    public List<Booking> getBookingsByCustomer(Long customerId) {
        return List.of();
    }

    @Override
    public Booking getBookingById(Long id) {
        return null;
    }

    @Override
    public Booking updateBooking(Long bookingId, BookingRequest booking) {
        return null;
    }

    @Override
    public List<Booking> getBookingByDate(LocalDateTime date, Long salonId) {
        return List.of();
    }

    @Override
    public SalonReport getSalonReport(Long salonId) {
        return null;
    }

    @Override
    public List<Booking> getBookingBySalon(Long salonId) {
        return List.of();
    }
}
