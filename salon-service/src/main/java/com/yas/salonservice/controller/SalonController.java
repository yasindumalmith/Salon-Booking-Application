package com.yas.salonservice.controller;

import com.yas.salonservice.dto.SalonDTO;
import com.yas.salonservice.dto.UserDTO;
import com.yas.salonservice.mapper.SalonMapper;
import com.yas.salonservice.model.Salon;
import com.yas.salonservice.repository.SalonRepository;
import com.yas.salonservice.service.SalonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/salons")
@RequiredArgsConstructor
public class SalonController {

    private final SalonService salonService;

    @PostMapping
    public ResponseEntity<SalonDTO> createSalon(@RequestBody SalonDTO salonDTO) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);

        Salon salon = salonService.createSalon(salonDTO, userDTO);
        SalonDTO salonDTO1= SalonMapper.mapSalonToDTO(salon);
        return ResponseEntity.ok(salonDTO1);
    }

    @PutMapping("/{salonId}")
    public ResponseEntity<SalonDTO> updateSalon(
            @PathVariable("salonId") Long salonId,
            @RequestBody SalonDTO salonDTO) throws Exception {

        UserDTO userDTO = new UserDTO();
        userDTO.setId(3L);

        Salon salon = salonService.updateSalon(salonDTO,userDTO,salonId);
        SalonDTO salonDTO1= SalonMapper.mapSalonToDTO(salon);
        return ResponseEntity.ok(salonDTO1);
    }

    @GetMapping()
    public ResponseEntity<List<SalonDTO>> getSalonById() throws Exception {



        List<Salon> salons = salonService.getSalons();
        List<SalonDTO> salonDTOS = salons.stream().map(SalonMapper::mapSalonToDTO).toList();
        return ResponseEntity.ok(salonDTOS);
    }

    @GetMapping("/{salonId}")
    public ResponseEntity<SalonDTO> getSalonById(@PathVariable Long salonId) throws Exception {

        Salon salon = salonService.getSalonById(salonId);
        return ResponseEntity.ok(SalonMapper.mapSalonToDTO(salon));
    }

    @GetMapping("/search")

    public ResponseEntity<List<SalonDTO>> searchSalon(@RequestParam("city") String search) throws Exception {


        List<Salon> salons= salonService.searchSalonByCity(search);
        List<SalonDTO> salonDTOS = salons.stream().map(SalonMapper::mapSalonToDTO).toList();
        return ResponseEntity.ok(salonDTOS);

    }

    @GetMapping("/owner")
    public ResponseEntity<SalonDTO> getOwnerByOwnerId(){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);

        Salon salon = salonService.getSalonByOwnerId(userDTO.getId());
        return ResponseEntity.ok(SalonMapper.mapSalonToDTO(salon));
    }
    @DeleteMapping("/{salonId}")
    public String deleteSalonById(@PathVariable("salonId") Long salonId) throws Exception {
        salonService.deleteSalonById(salonId);
        return "deleted";
    }
}
