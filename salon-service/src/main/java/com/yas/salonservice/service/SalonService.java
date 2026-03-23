package com.yas.salonservice.service;

import com.yas.salonservice.dto.SalonDTO;
import com.yas.salonservice.dto.UserDTO;
import com.yas.salonservice.model.Salon;

import java.util.List;

public interface SalonService {
    Salon createSalon(SalonDTO salon, UserDTO user);
    Salon updateSalon(SalonDTO salon, UserDTO user, Long salonId) throws Exception;
    List<Salon> getSalons();
    Salon getSalonById(Long salonId) throws Exception;
    Salon getSalonByOwnerId(Long ownerId);
    List<Salon> searchSalonByCity(String city);
    void deleteSalonById(Long salonId);
}
