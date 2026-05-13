// CategoryRepository.java
package com.elearning.backend.repository;
import com.elearning.backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}