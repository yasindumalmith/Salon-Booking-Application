package com.yas.serviceoffering.controller;


import com.yas.serviceoffering.dto.CategoryDTO;
import com.yas.serviceoffering.dto.SalonDTO;
import com.yas.serviceoffering.dto.ServiceDTO;
import com.yas.serviceoffering.model.ServiceOffering;
import com.yas.serviceoffering.service.ServiceOfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/service-offering/salon-owner")
@RequiredArgsConstructor
public class SalonOwnerServiceOffering {

    private final ServiceOfferingService serviceOfferingService;
    @PostMapping
    public ResponseEntity<ServiceOffering> createServiceOffering(
            @RequestBody ServiceDTO serviceDTO) throws Exception{

        SalonDTO salonDTO=new SalonDTO();
        salonDTO.setId(1L);

        CategoryDTO  categoryDTO=new CategoryDTO();
        categoryDTO.setId(serviceDTO.getCategoryId());

        ServiceOffering serviceOffering1=serviceOfferingService.createServiceOffering(salonDTO, categoryDTO,serviceDTO);
        return ResponseEntity.ok().body(serviceOffering1);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceOffering> updateServiceOffering(
            @RequestBody ServiceOffering serviceOffering,
            @PathVariable Long id
    ) throws Exception {



        ServiceOffering serviceOffering1 = serviceOfferingService.updateServiceOffering(id,serviceOffering);
        return ResponseEntity.ok(serviceOffering1);
    }
}
