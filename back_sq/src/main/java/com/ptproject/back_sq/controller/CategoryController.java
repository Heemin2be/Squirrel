package com.ptproject.back_sq.controller;

import com.ptproject.back_sq.dto.menu.CategoryResponse;
import com.ptproject.back_sq.entity.menu.Category;
import com.ptproject.back_sq.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping
    public Category create(@RequestBody Category category){
        return categoryRepository.save(category);
    }

    @GetMapping
    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
