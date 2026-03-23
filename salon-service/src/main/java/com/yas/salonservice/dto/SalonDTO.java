package com.yas.salonservice.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

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
