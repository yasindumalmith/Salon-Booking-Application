package com.yas.paymentservice.controller;

import com.stripe.exception.StripeException;
import com.yas.paymentservice.domain.PaymentMethod;
import com.yas.paymentservice.model.PaymentOrder;
import com.yas.paymentservice.payload.dto.BookingDTO;
import com.yas.paymentservice.payload.dto.UserDTO;
import com.yas.paymentservice.payload.response.PaymentLinkResponse;
import com.yas.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Create a payment order and (optionally) generate a payment link.
     * POST /api/payments/create-order?paymentMethod=STRIPE
     */
    @PostMapping("/create-order")
    public ResponseEntity<PaymentLinkResponse> createOrder(
            @RequestBody UserDTO userDTO,
            @RequestBody BookingDTO bookingDTO,
            @RequestParam PaymentMethod paymentMethod) throws StripeException {
        PaymentLinkResponse response = paymentService.createOrder(userDTO, bookingDTO, paymentMethod);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieve a PaymentOrder by its database id.
     * GET /api/payments/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentOrder> getPaymentOrderById(@PathVariable Long id) throws Exception {
        PaymentOrder paymentOrder = paymentService.getPaymentOrderById(id);
        return ResponseEntity.ok(paymentOrder);
    }

    /**
     * Retrieve a PaymentOrder by the external Stripe payment/session id.
     * GET /api/payments/by-payment-id/{paymentId}
     */
    @GetMapping("/by-payment-id/{paymentId}")
    public ResponseEntity<PaymentOrder> getPaymentOrderByPaymentId(@PathVariable String paymentId) {
        PaymentOrder paymentOrder = paymentService.getPaymentOrderByPaymentId(paymentId);
        if (paymentOrder == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(paymentOrder);
    }

    /**
     * Generate a Stripe Checkout Session URL directly.
     * POST /api/payments/stripe-link?amount=1500&orderId=42
     */
    @PostMapping("/stripe-link")
    public ResponseEntity<String> createStripePaymentLink(
            @RequestBody UserDTO userDTO,
            @RequestParam Long amount,
            @RequestParam Long orderId) throws StripeException {
        String url = paymentService.createStripePaymentLink(userDTO, amount, orderId);
        return ResponseEntity.ok(url);
    }

    /**
     * Proceed (finalise) a payment after the user returns from the payment gateway.
     * POST /api/payments/proceed?paymentId=cs_test_abc&paymentLinkId=pl_xyz
     *
     * paymentId    – the Stripe Checkout Session ID (used to look up the local order)
     * paymentLinkId – the Stripe Payment Link ID stored on the order record
     */
    @PostMapping("/proceed")
    public ResponseEntity<PaymentOrder> proceedPayment(
            @RequestParam String paymentId,
            @RequestParam String paymentLinkId) {

        PaymentOrder paymentOrder = paymentService.getPaymentOrderByPaymentId(paymentId);
        if (paymentOrder == null) {
            return ResponseEntity.notFound().build();
        }

        PaymentOrder updated = paymentService.proceedPayment(paymentOrder, paymentId, paymentLinkId);
        return ResponseEntity.ok(updated);
    }
}

