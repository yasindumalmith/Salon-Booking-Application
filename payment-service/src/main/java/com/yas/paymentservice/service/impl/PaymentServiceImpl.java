package com.yas.paymentservice.service.impl;

import com.stripe.model.PaymentLink;
import com.yas.paymentservice.domain.PaymentMethod;
import com.yas.paymentservice.model.PaymentOrder;
import com.yas.paymentservice.payload.dto.BookingDTO;
import com.yas.paymentservice.payload.dto.UserDTO;
import com.yas.paymentservice.payload.response.PaymentLinkResponse;
import com.yas.paymentservice.repository.PaymentRepository;
import com.yas.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Value("${stripe.api.key}")
    private String stripeAPIKey;

    @Value("${stripe.api.secret}")
    private String stripeAPISecret;


    @Override
    public PaymentLinkResponse createOrder(UserDTO userDTO, BookingDTO bookingDTO, PaymentMethod paymentMethod) {
        Long amount=(long)bookingDTO.getTotalPrice();

        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setAmount(amount);
        paymentOrder.setPaymentMethod(paymentMethod);
        paymentOrder.setBookId(bookingDTO.getId());
        paymentOrder.setSalonId(bookingDTO.getSalonId());
        PaymentOrder savedOrder = paymentRepository.save(paymentOrder);

        PaymentLinkResponse paymentLinkResponse = new PaymentLinkResponse();

        if(paymentMethod.equals(PaymentMethod.STRIPE)){
            String paymentUrl=createStripePaymentLink(userDTO, savedOrder.getAmount(), savedOrder.getId());
            paymentLinkResponse.setPayment_link_url(paymentUrl);
        }
        return paymentLinkResponse;
    }

    @Override
    public PaymentOrder getPaymentOrderById(Long id) throws Exception {
        PaymentOrder paymentOrder = paymentRepository.findById(id).orElse(null);
        if(paymentOrder != null){
            return paymentOrder;
        }else{
            throw new Exception("Payment Order not found");
        }
    }

    @Override
    public PaymentOrder getPaymentOrderByPaymentId(Long paymentId) {
        return null;
    }

    @Override
    public String createStripePaymentLink(UserDTO userDTO, Long amount, Long orderId) {
        return "";
    }
}
