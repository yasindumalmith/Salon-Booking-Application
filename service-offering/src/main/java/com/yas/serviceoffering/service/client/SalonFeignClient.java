package com.yas.serviceoffering.service.client;

import com.yas.serviceoffering.dto.SalonDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("SERVICE-OFFERING")
public interface SalonFeignClient {
    @GetMapping("/owner")
    public ResponseEntity<SalonDTO> getOwnerByOwnerId(@RequestHeader("Authorization")  String token) throws Exception;
}
