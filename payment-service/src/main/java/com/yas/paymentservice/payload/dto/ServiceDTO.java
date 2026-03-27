package com.yas.paymentservice.payload.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServiceDTO {
    private Long id;


    private String name;

    private String description;


    private int price;

    private int duration;

    private Long salonId;

    private Long categoryId;

    private String image;

}
