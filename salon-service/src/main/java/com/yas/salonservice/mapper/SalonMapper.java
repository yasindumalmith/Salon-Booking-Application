package com.yas.salonservice.mapper;

import com.yas.salonservice.dto.SalonDTO;
import com.yas.salonservice.model.Salon;

public class SalonMapper {

    public static SalonDTO mapSalonToDTO(Salon salon) {
        SalonDTO salonDTO = new SalonDTO();
        salonDTO.setId(salon.getId());
        salonDTO.setName(salon.getName());
        salonDTO.setCity(salon.getCity());
        salonDTO.setImages(salon.getImages());
        salonDTO.setClosingTime(salon.getClosingTime());
        salonDTO.setOpeningTime(salon.getOpeningTime());
        salonDTO.setPhoneNumber(String.valueOf(salon.getPhoneNumber()));
        salonDTO.setOwnerId(salon.getOwnerId());
        salonDTO.setEmail(salon.getEmail());
        return salonDTO;
    }
}
