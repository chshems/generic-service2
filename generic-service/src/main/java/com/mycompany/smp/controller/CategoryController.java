package com.mycompany.smp.controller;

import com.mycompany.smp.dto.ErrorDTO;
import com.mycompany.smp.entity.CategoryEntity;
import com.mycompany.smp.exception.BusinessException;
import com.mycompany.smp.repository.CategoryRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private CategoryRepository categoryRepository;
    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CategoryEntity> addCategory(@Valid @RequestBody CategoryEntity category) {
        // 👈 Step 1: Manually validate that the name is not null or empty
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new BusinessException(List.of(
                    new ErrorDTO("NAME_REQUIRED", "Category name cannot be null or empty")
            ));
        }

        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        category = categoryRepository.save(category);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<CategoryEntity>> allCategories() {
        return new ResponseEntity<>(categoryRepository.findAll(), HttpStatus.OK);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryEntity> updateCategory(
            @RequestBody CategoryEntity category, // 👈 Make sure @Valid is removed here
            @PathVariable Long categoryId) {

        Optional<CategoryEntity> optCe = categoryRepository.findById(categoryId);
        if(optCe.isPresent()) {
            CategoryEntity categoryDb = optCe.get();

            // 👈 FIX: Only update the name if the request actually provided a new one
            if (category.getName() != null && !category.getName().trim().isEmpty()) {
                categoryDb.setName(category.getName());
            }

            // 👈 FIX: Only update the description if the request actually provided a new one
            if (category.getDescription() != null) {
                categoryDb.setDescription(category.getDescription());
            }

            categoryDb.setUpdatedAt(LocalDateTime.now());
            categoryRepository.save(categoryDb);
            return new ResponseEntity<>(categoryDb, HttpStatus.OK);
        } else {
            throw new BusinessException(List.of(new ErrorDTO("CAT_NOT_FOUND", "The category to be updated does not exist")));
        }
    }


    @GetMapping("/{categoryId}")
public ResponseEntity<CategoryEntity> getCategory(@PathVariable Long categoryId) {
        Optional<CategoryEntity>  categoryEntity = categoryRepository.findById(categoryId);
        if(categoryEntity.isPresent()) {
            return new ResponseEntity<>(categoryEntity.get(),HttpStatus.OK);
        }
        else {
            throw new BusinessException(List.of(new ErrorDTO("CAT_NOT_FOUND", "The category with this Id does not exist")));
        }
    }

}
