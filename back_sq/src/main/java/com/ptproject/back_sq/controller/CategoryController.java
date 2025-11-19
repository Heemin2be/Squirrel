package com.ptproject.back_sq.controller;

import com.ptproject.back_sq.dto.menu.CategoryRequest;
import com.ptproject.back_sq.dto.menu.CategoryResponse;
import com.ptproject.back_sq.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // 전체 카테고리 조회
    @GetMapping
    public List<CategoryResponse> getAll() {
        return categoryService.getAll();
    }

    // 단일 카테고리 조회
    @GetMapping("/{id}")
    public CategoryResponse getOne(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    // 카테고리 생성
    @PostMapping
    public CategoryResponse create(@RequestBody CategoryRequest request) {
        return categoryService.create(request);
    }

    // 카테고리 수정
    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id,
                                   @RequestBody CategoryRequest request) {
        return categoryService.update(id, request);
    }

    // 카테고리 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }
}
