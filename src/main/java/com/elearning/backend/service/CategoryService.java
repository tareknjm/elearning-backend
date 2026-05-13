package com.elearning.backend.service;

import com.elearning.backend.dto.CategoryResponse;
import com.elearning.backend.model.Category;
import com.elearning.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public CategoryResponse createCategory(String name) {
        Category category = Category.builder().name(name).build();
        return CategoryResponse.fromEntity(categoryRepository.save(category));
    }
}