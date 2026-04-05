package com.yas.bookingservice.listener;

import com.yas.bookingservice.config.RabbitMqConfig;
import com.yas.bookingservice.dto.PaymentEventDTO;
import com.yas.bookingservice.model.Booking;
import com.yas.bookingservice.domain.BookingStatus; // PENDING, CONFIRMED ආදිය ඇති Enum එක
import com.yas.bookingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final BookingRepository bookingRepository;

    @RabbitListener(queues = RabbitMqConfig.QUEUE)
    public void handlePaymentSuccessEvent(PaymentEventDTO event) {
        log.info("Received message from Queue Booking ID: {}", event.getBookingId());

        if ("SUCCESS".equals(event.getStatus())) {


            Booking booking = bookingRepository.findById(event.getBookingId())
                    .orElseThrow(() -> new RuntimeException("Booking not found for ID: " + event.getBookingId()));


            booking.setStatus(BookingStatus.CONFIRMED);

            bookingRepository.save(booking);

            log.info("Successfully update the Booking. (ID: {})", booking.getId());
        } else {
            log.warn("Payment is failed Booking ID: {}", event.getBookingId());
        }
    }
}