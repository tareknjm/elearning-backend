package com.elearning.backend.dto;

import com.elearning.backend.model.Category;
import lombok.Data;

@Data
public class CategoryResponse {
    private Long id;
    private String name;

    public static CategoryResponse fromEntity(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        return response;
    }
}