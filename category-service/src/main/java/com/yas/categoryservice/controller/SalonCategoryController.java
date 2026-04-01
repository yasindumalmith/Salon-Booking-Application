package com.yas.categoryservice.controller;


import com.yas.categoryservice.dto.SalonDTO;
import com.yas.categoryservice.model.Category;
import com.yas.categoryservice.service.CategoryService;
import com.yas.categoryservice.service.client.SalonFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories/salon-owner")
public class SalonCategoryController {
    private final CategoryService categoryService;
    private final SalonFeignClient salonFeignClient;

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category,
                                                   @RequestHeader("Authorization") String token) throws Exception {
        SalonDTO salonDTO = salonFeignClient.getOwnerByOwnerId(token).getBody();

        Category savedCategory=categoryService.saveCategory(category,salonDTO);
        return ResponseEntity.ok(savedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String token
    ) throws Exception {
        SalonDTO salonDTO = salonFeignClient.getOwnerByOwnerId(token).getBody();
        if (salonDTO==null){
            throw new Exception("User is not found");
        }

        categoryService.deleteCategoryById(id, salonDTO.getId());
        return ResponseEntity.ok("Category deleted");
    }
}
