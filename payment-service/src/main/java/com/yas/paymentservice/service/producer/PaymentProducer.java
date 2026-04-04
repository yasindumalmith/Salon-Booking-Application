package com.yas.paymentservice.service.producer;

import com.yas.paymentservice.config.RabbitMqConfig;
import com.yas.paymentservice.model.PaymentOrder;
import com.yas.paymentservice.payload.dto.PaymentEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentProducer {


    private final RabbitTemplate rabbitTemplate;

    public void publishPaymentSuccessEvent(PaymentOrder paymentOrder) {


        PaymentEventDTO event = new PaymentEventDTO(
                paymentOrder.getPaymentLinkedId(),
                paymentOrder.getBookId(),
                "SUCCESS"
        );

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE,
                "payment.routing.key",
                event
        );

        System.out.println("Message send using RabbitMq: " + event.getBookingId());
    }
}