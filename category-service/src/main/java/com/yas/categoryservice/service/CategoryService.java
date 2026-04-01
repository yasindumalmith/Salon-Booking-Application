package com.yas.categoryservice.service;


import com.yas.categoryservice.dto.SalonDTO;
import com.yas.categoryservice.model.Category;

import java.util.Set;

public interface CategoryService {
    Category saveCategory(Category category, SalonDTO salonDTO);
    Set<Category> getAllCategoriesBySalon(Long id);
    Category getCategoryById(Long id) throws Exception;
    void deleteCategoryById(Long id, Long salonId) throws Exception;
    Category getCategoriesByIdAndSalonId(Long id,Long salonId) throws Exception;
}
