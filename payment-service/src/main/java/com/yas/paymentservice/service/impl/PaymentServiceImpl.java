package com.yas.paymentservice.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.yas.paymentservice.domain.PaymentMethod;
import com.yas.paymentservice.domain.PaymentOrderStatus;
import com.yas.paymentservice.model.PaymentOrder;
import com.yas.paymentservice.payload.dto.BookingDTO;
import com.yas.paymentservice.payload.dto.UserDTO;
import com.yas.paymentservice.payload.response.PaymentLinkResponse;
import com.yas.paymentservice.repository.PaymentRepository;
import com.yas.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Value("${stripe.api.key}")
    private String stripeAPIKey;

    @Value("${stripe.api.secret}")
    private String stripeAPISecret;


    @Override
    public PaymentLinkResponse createOrder(UserDTO userDTO, BookingDTO bookingDTO, PaymentMethod paymentMethod) throws StripeException {
        Long amount = (long) bookingDTO.getTotalPrice();

        PaymentLinkResponse paymentLinkResponse = new PaymentLinkResponse();

        if (paymentMethod.equals(PaymentMethod.STRIPE)) {
            paymentLinkResponse= createStripePaymentLink(userDTO, amount, bookingDTO.getId());
        }

        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setAmount(amount);
        paymentOrder.setPaymentMethod(paymentMethod);
        paymentOrder.setBookId(bookingDTO.getId());
        paymentOrder.setSalonId(bookingDTO.getSalonId());
        paymentOrder.setPaymentLinkedId(paymentLinkResponse.getPaymentLinkedId());
        paymentOrder.setUserId(userDTO.getId());
        paymentOrder.setStatus(PaymentOrderStatus.PENDING);
        paymentRepository.save(paymentOrder);


        return paymentLinkResponse;
    }

    @Override
    public PaymentOrder getPaymentOrderById(Long id) throws Exception {
        PaymentOrder paymentOrder = paymentRepository.findById(id).orElse(null);
        if (paymentOrder != null) {
            return paymentOrder;
        } else {
            throw new Exception("Payment Order not found");
        }
    }

    @Override
    public PaymentOrder getPaymentOrderByPaymentId(String paymentId) {
        return paymentRepository.findByPaymentLinkedId(paymentId);
    }

    @Override
    public PaymentLinkResponse createStripePaymentLink(UserDTO userDTO, Long amount, Long orderId) throws StripeException {
        Stripe.apiKey = stripeAPISecret;

        SessionCreateParams params = SessionCreateParams.builder()
                // Accept card payments
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)

                // Where Stripe redirects the user after they pay (or cancel)
                .setSuccessUrl("http://localhost:3000/payment/success" + orderId)
                .setCancelUrl("http://localhost:3000/payment/cancel")


                // 3. Define what the user is paying for
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("lkr") // Change to 'usd' if needed
                                                // Stripe expects amounts in the smallest currency unit (e.g., cents)
                                                // So we multiply by 100. (Rs 1500 -> 150000)
                                                .setUnitAmount(amount * 100)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Salon Booking Appointment #" + orderId)
                                                                .build())
                                                .build())
                                .build())
                .build();

        // 4. Create the session and return the URL
        Session session = Session.create(params);
        PaymentLinkResponse paymentLinkResponse = new PaymentLinkResponse();
        paymentLinkResponse.setPayment_link_url(session.getUrl());
        paymentLinkResponse.setPaymentLinkedId(session.getId());

        return paymentLinkResponse;
    }

    @Override
    public PaymentOrder proceedPayment(PaymentOrder paymentOrder, String paymentId, String paymentLinkId) {
        if (paymentOrder.getStatus().equals(PaymentOrderStatus.PENDING)) {
            if (paymentOrder.getPaymentMethod().equals(PaymentMethod.STRIPE)) {
                paymentOrder.setPaymentLinkedId(paymentLinkId);
            }
            paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
        } else {
            paymentOrder.setStatus(PaymentOrderStatus.FAILED);
        }
        return paymentRepository.save(paymentOrder);
    }
}

