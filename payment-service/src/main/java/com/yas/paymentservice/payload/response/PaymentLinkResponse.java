package com.yas.paymentservice.payload.response;

import lombok.Data;

@Data
public class PaymentLinkResponse {
    private String payment_link_url;
    private String payment_link_type;
    private String paymentLinkedId;

}
