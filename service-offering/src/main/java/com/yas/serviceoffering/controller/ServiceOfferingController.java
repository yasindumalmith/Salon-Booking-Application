package com.yas.serviceoffering.controller;


import com.yas.serviceoffering.model.ServiceOffering;
import com.yas.serviceoffering.service.ServiceOfferingService;
import jakarta.persistence.SecondaryTable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/service-offering")
public class ServiceOfferingController {

    private final ServiceOfferingService serviceOfferingService;

    @GetMapping("/salon/{salonId}")
    public ResponseEntity<Set<ServiceOffering>>  getServiceOfferingById(
            @PathVariable Long salonId,
            @RequestParam(required = false) Long categoryId
    ) throws Exception{
        Set<ServiceOffering> serviceOfferings=serviceOfferingService.getAllServiceBySalonId(salonId,categoryId);
        return ResponseEntity.ok(serviceOfferings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceOffering> getServiceOfferingById(@PathVariable Long id) throws Exception{
        ServiceOffering serviceOffering=serviceOfferingService.getServiceOfferingById(id);
        return ResponseEntity.ok(serviceOffering);
    }

    @GetMapping("/list/{ids}")
    public ResponseEntity<Set<ServiceOffering>> getServiceOfferingByIds(@PathVariable Set<Long> ids){
        Set<ServiceOffering> serviceOfferings=serviceOfferingService.getServicesByIds(ids);
        return ResponseEntity.ok(serviceOfferings);
    }
}
