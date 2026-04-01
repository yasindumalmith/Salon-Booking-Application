package com.yas.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SalonDTO {

    private Long id;

    private String name;


    private List<String> images;


    private String address;



    private String email;


    private String city;


    private Long ownerId;


    private LocalTime openingTime;



    private LocalTime closingTime;
    private String phoneNumber;
}
