package com.yas.serviceoffering.service.client;

import com.yas.serviceoffering.dto.CategoryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("CATEGORY-SERVICE")
public interface CategoryFeignClient {
    @GetMapping("/api/categories/salon/{salonId}/category/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategoryByIdAndSalon(
            @PathVariable("salonId")  Long id,
            @PathVariable("categoryId") Long categoryId
    ) throws Exception;
}
