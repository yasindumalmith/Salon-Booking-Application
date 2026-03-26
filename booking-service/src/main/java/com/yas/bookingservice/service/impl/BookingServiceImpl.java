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

import java.time.LocalDate;
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

        if(isSlotAvailable){
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
        throw new Exception("Cannot create booking try with different time");



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
        return bookingRepository.findByCustomerId(customerId);
    }

    @Override
    public Booking getBookingById(Long id) throws Exception {
        Booking bookingEntity = bookingRepository.findById(id).orElse(null);
        if (bookingEntity != null) {
            return bookingEntity;
        }else {
            throw new Exception("Booking Id is not Available");
        }
    }

    @Override
    public Booking updateBooking(Long bookingId, BookingStatus status) throws Exception {
        Booking bookingEntity = getBookingById(bookingId);
        bookingEntity.setStatus(status);
        return bookingRepository.save(bookingEntity);
    }

    @Override
    public List<Booking> getBookingByDate(LocalDate date, Long salonId) {
        List<Booking> booking = getBookingBySalon(salonId);
        if(date==null){
            return booking;
        }else {
           return booking.stream()
                   .filter(b->b.getStartTime().toLocalDate().equals(date))
                   .collect(Collectors.toList());
        }
    }

    @Override
    public SalonReport getSalonReport(Long salonId) {
        List<Booking> booking = getBookingBySalon(salonId);
        Double totalPrice = booking.stream().mapToDouble(Booking::getTotalPrice).sum();

        Integer totalBooking=booking.size();
        List<Booking> cancelBookings = booking.stream()
                .filter(booking1 -> booking1.getStatus().equals(BookingStatus.CANCELLED))
                .collect(Collectors.toList());

        Double totalRefund = cancelBookings.stream()
                .mapToDouble(Booking::getTotalPrice).sum();

        SalonReport salonReport = new SalonReport();
        salonReport.setSalonId(salonId);
        salonReport.setCancelledBookings(cancelBookings.size());
        salonReport.setTotalBookings(totalBooking);
        salonReport.setTotalRefund(totalRefund);
        salonReport.setTotalEarnings(totalPrice);
        return salonReport;

    }

    @Override
    public List<Booking> getBookingBySalon(Long salonId) {
        return bookingRepository.findBySalonId(salonId);
    }
}
