package com.yas.salonservice.service.impl;

import com.yas.salonservice.dto.SalonDTO;
import com.yas.salonservice.dto.UserDTO;
import com.yas.salonservice.metrics.SalonMetrics;
import com.yas.salonservice.model.Salon;
import com.yas.salonservice.repository.SalonRepository;
import com.yas.salonservice.service.SalonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalonServiceImpl implements SalonService {

    private final SalonRepository salonRepository;
    private final SalonMetrics salonMetrics;
    @Override
    public Salon createSalon(SalonDTO salon, UserDTO user) {
        Salon  salon1 = new Salon();
        salon1.setName(salon.getName());
        salon1.setAddress(salon.getAddress());
        salon1.setEmail(salon.getEmail());
        salon1.setCity(salon.getCity());
        salon1.setImages(salon.getImages());
        salon1.setOwnerId(user.getId());
        salon1.setOpeningTime(salon.getOpeningTime());
        salon1.setClosingTime(salon.getClosingTime());
        salon1.setPhoneNumber(salon.getPhoneNumber());

        Salon saved = salonRepository.save(salon1);
        // ── Business metric: salon successfully created ──────────────────
        salonMetrics.incrementSalonCreateSuccess();
        return saved;
    }

    @Override
    public Salon updateSalon(SalonDTO salon, UserDTO user, Long salonId) throws Exception {

        Salon existingSalon = salonRepository.findById(salonId).orElse(null);
        if(existingSalon != null && salon.getOwnerId().equals(user.getId())) {
            existingSalon.setName(salon.getName());
            existingSalon.setAddress(salon.getAddress());
            existingSalon.setEmail(salon.getEmail());
            existingSalon.setCity(salon.getCity());
            existingSalon.setImages(salon.getImages());
            existingSalon.setOwnerId(salon.getOwnerId());
            existingSalon.setOpeningTime(salon.getOpeningTime());
            existingSalon.setClosingTime(salon.getClosingTime());
            existingSalon.setPhoneNumber(salon.getPhoneNumber());
            Salon updated = salonRepository.save(existingSalon);
            // ── Business metric: salon successfully updated ──────────────
            salonMetrics.incrementSalonUpdateSuccess();
            return updated;
        }
        // ── Business metric: salon update failed (not found/unauthorised) ─
        salonMetrics.incrementSalonUpdateFailed();
        throw new Exception("salon not exist");
    }

    @Override
    public List<Salon> getSalons() {
        return salonRepository.findAll();
    }

    @Override
    public Salon getSalonById(Long salonId) throws Exception {
        Salon salon = salonRepository.findById(salonId).orElse(null);
        if(salon != null) {
            return salon;
        }
        throw new Exception("Salon not exist");
    }

    @Override
    public Salon getSalonByOwnerId(Long ownerId) {
        return salonRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Salon> searchSalonByCity(String city) {
        return salonRepository.searchSalons(city);
    }

    @Override
    public void deleteSalonById(Long salonId) {
        salonRepository.deleteById(salonId);
    }
}
