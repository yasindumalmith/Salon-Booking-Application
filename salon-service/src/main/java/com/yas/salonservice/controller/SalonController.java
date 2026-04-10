package com.yas.salonservice.controller;

import com.yas.salonservice.dto.SalonDTO;
import com.yas.salonservice.dto.UserDTO;
import com.yas.salonservice.mapper.SalonMapper;
import com.yas.salonservice.metrics.SalonMetrics;
import com.yas.salonservice.model.Salon;
import com.yas.salonservice.service.SalonService;
import com.yas.salonservice.service.client.UserFeignClient;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salons")
@RequiredArgsConstructor
public class SalonController {

    private final SalonService salonService;
    private final UserFeignClient userFeignClient;
    private final SalonMetrics salonMetrics;

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/salons
    // Metrics: total requests, response time, error rate
    // External calls: user-service (timed)
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<SalonDTO> createSalon(
            @RequestBody SalonDTO salonDTO,
            @RequestHeader("Authorization") String token) throws Exception {

        final String ENDPOINT = "POST /api/salons";
        salonMetrics.incrementApiRequest(ENDPOINT, "POST");
        Timer.Sample apiTimer = salonMetrics.startApiTimer();

        try {
            // ── External call: user-service ──────────────────────────────────
            Timer.Sample userCallTimer = salonMetrics.startExternalCallTimer();
            UserDTO userDTO;
            try {
                userDTO = userFeignClient.getUserProfile(token).getBody();
                salonMetrics.stopExternalCallTimer(userCallTimer, "user-service", "getUserProfile");
            } catch (Exception ex) {
                salonMetrics.stopExternalCallTimer(userCallTimer, "user-service", "getUserProfile");
                salonMetrics.incrementExternalCallFailure("user-service", "getUserProfile");
                throw ex;
            }

            Salon salon = salonService.createSalon(salonDTO, userDTO);
            SalonDTO result = SalonMapper.mapSalonToDTO(salon);
            salonMetrics.stopApiTimer(apiTimer, ENDPOINT, "POST", "2xx");
            return ResponseEntity.ok(result);

        } catch (Exception ex) {
            salonMetrics.incrementApiError(ENDPOINT, "POST", ex.getClass().getSimpleName());
            salonMetrics.stopApiTimer(apiTimer, ENDPOINT, "POST", "5xx");
            throw ex;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/salons/{salonId}
    // Metrics: total requests, response time, error rate
    // External calls: user-service (timed)
    // ─────────────────────────────────────────────────────────────────────────
    @PutMapping("/{salonId}")
    public ResponseEntity<SalonDTO> updateSalon(
            @PathVariable("salonId") Long salonId,
            @RequestBody SalonDTO salonDTO,
            @RequestHeader("Authorization") String token) throws Exception {

        final String ENDPOINT = "PUT /api/salons/{salonId}";
        salonMetrics.incrementApiRequest(ENDPOINT, "PUT");
        Timer.Sample apiTimer = salonMetrics.startApiTimer();

        try {
            // ── External call: user-service ──────────────────────────────────
            Timer.Sample userCallTimer = salonMetrics.startExternalCallTimer();
            UserDTO userDTO;
            try {
                userDTO = userFeignClient.getUserProfile(token).getBody();
                salonMetrics.stopExternalCallTimer(userCallTimer, "user-service", "getUserProfile");
            } catch (Exception ex) {
                salonMetrics.stopExternalCallTimer(userCallTimer, "user-service", "getUserProfile");
                salonMetrics.incrementExternalCallFailure("user-service", "getUserProfile");
                throw ex;
            }

            Salon salon = salonService.updateSalon(salonDTO, userDTO, salonId);
            SalonDTO result = SalonMapper.mapSalonToDTO(salon);
            salonMetrics.stopApiTimer(apiTimer, ENDPOINT, "PUT", "2xx");
            return ResponseEntity.ok(result);

        } catch (Exception ex) {
            salonMetrics.incrementApiError(ENDPOINT, "PUT", ex.getClass().getSimpleName());
            salonMetrics.stopApiTimer(apiTimer, ENDPOINT, "PUT", "5xx");
            throw ex;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/salons
    // Metrics: total requests, response time, error rate
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping()
    public ResponseEntity<List<SalonDTO>> getSalons() throws Exception {

        final String ENDPOINT = "GET /api/salons";
        salonMetrics.incrementApiRequest(ENDPOINT, "GET");
        Timer.Sample apiTimer = salonMetrics.startApiTimer();

        try {
            List<Salon> salons = salonService.getSalons();
            List<SalonDTO> salonDTOS = salons.stream().map(SalonMapper::mapSalonToDTO).toList();
            salonMetrics.stopApiTimer(apiTimer, ENDPOINT, "GET", "2xx");
            return ResponseEntity.ok(salonDTOS);

        } catch (Exception ex) {
            salonMetrics.incrementApiError(ENDPOINT, "GET", ex.getClass().getSimpleName());
            salonMetrics.stopApiTimer(apiTimer, ENDPOINT, "GET", "5xx");
            throw ex;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/salons/{salonId}
    // Metrics: total requests, response time, error rate
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/{salonId}")
    public ResponseEntity<SalonDTO> getSalonById(@PathVariable Long salonId) throws Exception {

        final String ENDPOINT = "GET /api/salons/{salonId}";
        salonMetrics.incrementApiRequest(ENDPOINT, "GET");
        Timer.Sample apiTimer = salonMetrics.startApiTimer();

        try {
            Salon salon = salonService.getSalonById(salonId);
            salonMetrics.stopApiTimer(apiTimer, ENDPOINT, "GET", "2xx");
            return ResponseEntity.ok(SalonMapper.mapSalonToDTO(salon));

        } catch (Exception ex) {
            salonMetrics.incrementApiError(ENDPOINT, "GET", ex.getClass().getSimpleName());
            salonMetrics.stopApiTimer(apiTimer, ENDPOINT, "GET", "5xx");
            throw ex;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/salons/search
    // Metrics: total requests, response time, error rate
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/search")
    public ResponseEntity<List<SalonDTO>> searchSalon(@RequestParam("city") String search) throws Exception {

        final String ENDPOINT = "GET /api/salons/search";
        salonMetrics.incrementApiRequest(ENDPOINT, "GET");
        Timer.Sample apiTimer = salonMetrics.startApiTimer();

        try {
            List<Salon> salons = salonService.searchSalonByCity(search);
            List<SalonDTO> salonDTOS = salons.stream().map(SalonMapper::mapSalonToDTO).toList();
            salonMetrics.stopApiTimer(apiTimer, ENDPOINT, "GET", "2xx");
            return ResponseEntity.ok(salonDTOS);

        } catch (Exception ex) {
            salonMetrics.incrementApiError(ENDPOINT, "GET", ex.getClass().getSimpleName());
            salonMetrics.stopApiTimer(apiTimer, ENDPOINT, "GET", "5xx");
            throw ex;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/salons/owner
    // Metrics: total requests, response time, error rate
    // External calls: user-service (timed)
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/owner")
    public ResponseEntity<SalonDTO> getOwnerByOwnerId(
            @RequestHeader("Authorization") String token) throws Exception {

        final String ENDPOINT = "GET /api/salons/owner";
        salonMetrics.incrementApiRequest(ENDPOINT, "GET");
        Timer.Sample apiTimer = salonMetrics.startApiTimer();

        try {
            // ── External call: user-service ──────────────────────────────────
            Timer.Sample userCallTimer = salonMetrics.startExternalCallTimer();
            UserDTO userDTO;
            try {
                userDTO = userFeignClient.getUserProfile(token).getBody();
                salonMetrics.stopExternalCallTimer(userCallTimer, "user-service", "getUserProfile");
            } catch (Exception ex) {
                salonMetrics.stopExternalCallTimer(userCallTimer, "user-service", "getUserProfile");
                salonMetrics.incrementExternalCallFailure("user-service", "getUserProfile");
                throw ex;
            }

            if (userDTO == null) {
                salonMetrics.incrementApiError(ENDPOINT, "GET", "UserNotFoundException");
                salonMetrics.stopApiTimer(apiTimer, ENDPOINT, "GET", "4xx");
                throw new Exception("User is not found in JWT");
            }

            Salon salon = salonService.getSalonByOwnerId(userDTO.getId());
            salonMetrics.stopApiTimer(apiTimer, ENDPOINT, "GET", "2xx");
            return ResponseEntity.ok(SalonMapper.mapSalonToDTO(salon));

        } catch (Exception ex) {
            salonMetrics.incrementApiError(ENDPOINT, "GET", ex.getClass().getSimpleName());
            salonMetrics.stopApiTimer(apiTimer, ENDPOINT, "GET", "5xx");
            throw ex;
        }
    }

    @DeleteMapping("/{salonId}")
    public String deleteSalonById(@PathVariable("salonId") Long salonId) throws Exception {
        salonService.deleteSalonById(salonId);
        return "deleted";
    }
}
