package com.yas.categoryservice.controller;


import com.yas.categoryservice.model.Category;
import com.yas.categoryservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/salon/{id}")
    public ResponseEntity<Set<Category>> getCategoriesBySalon(
            @PathVariable("id") Long id
    ){
        Set<Category> getAllCategoriesBySalon = categoryService.getAllCategoriesBySalon(id);
        return ResponseEntity.ok(getAllCategoriesBySalon);
    }



    @GetMapping("/salon/{salonId}/category/{categoryId}")
    public ResponseEntity<Category> getCategoryByIdAndSalon(
            @PathVariable("salonId")  Long id,
            @PathVariable("categoryId") Long categoryId
    ) throws Exception {
        Category categories = categoryService.getCategoriesByIdAndSalonId(id,categoryId);
        return ResponseEntity.ok(categories);
    }

}
