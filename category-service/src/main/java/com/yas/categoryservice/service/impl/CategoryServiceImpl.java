package com.yas.categoryservice.service.impl;

import com.yas.categoryservice.dto.SalonDTO;
import com.yas.categoryservice.model.Category;
import com.yas.categoryservice.repository.CategoryRepository;
import com.yas.categoryservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category saveCategory(Category category, SalonDTO salonDTO) {
        Category newCategory = new Category();
        newCategory.setName(category.getName());
        newCategory.setSalonId(salonDTO.getId());
        newCategory.setImage(category.getImage());

        return categoryRepository.save(newCategory);
    }

    @Override
    public Set<Category> getAllCategoriesBySalon(Long id) {
        return categoryRepository.findBySalonId(id);
    }

    @Override
    public Category getCategoryById(Long id) throws Exception {
        Category category = categoryRepository.findById(id).orElse(null);

        if(category ==null){
            throw new Exception("Category not exist with id" + id);
        }else {
            return category;
        }
    }

    @Override
    public void deleteCategoryById(Long id, Long salonId) throws Exception {
        Category category = categoryRepository.findById(id).orElse(null);
        if(category != null && category.getSalonId().equals(salonId)){
            categoryRepository.deleteById(id);
        }else{
            throw new Exception("Category not exist with id" + id);
        }
    }

    @Override
    public Category getCategoriesByIdAndSalonId(Long id, Long salonId) throws Exception {
        Category category = categoryRepository.findByIdAndSalonId(id,salonId);
        if(category ==null){
            throw new Exception("Categories not found");
        }
        return category;
    }
}
