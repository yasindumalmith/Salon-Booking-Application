package com.yas.categoryservice.repository;

import com.yas.categoryservice.model.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CategoryRepository extends CrudRepository<Category,Long> {
    Set<Category> findBySalonId(Long salonId);
    Category findByIdAndSalonId(Long id,Long salonId);
}
