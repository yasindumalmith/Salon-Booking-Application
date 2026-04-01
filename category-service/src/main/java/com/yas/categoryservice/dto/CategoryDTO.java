package com.yas.categoryservice.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class CategoryDTO {
    private String name;
    private String image;
}
