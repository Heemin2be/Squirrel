package com.ptproject.back_sq.service;

import com.ptproject.back_sq.dto.menu.CategoryRequest;
import com.ptproject.back_sq.dto.menu.CategoryResponse;
import com.ptproject.back_sq.entity.menu.Category;
import com.ptproject.back_sq.repository.CategoryRepository;
import com.ptproject.back_sq.repository.MenuRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor        // ✅ 생성자 자동 생성
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final MenuRepository menuRepository;

    // 전체 조회
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    // 단건 조회
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));
        return CategoryResponse.from(category);
    }

    // 생성
    public CategoryResponse create(CategoryRequest request) {
        Category category = new Category(request.getName());
        Category saved = categoryRepository.save(category);
        return CategoryResponse.from(saved);
    }

    // 수정
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));

        category.changeName(request.getName());
        return CategoryResponse.from(category);
    }

    // 삭제
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found: " + id);
        }

        if (menuRepository.existsByCategoryId(id)) {
            throw new IllegalStateException("카테고리에 연결된 메뉴가 있어 삭제할 수 없습니다.");
        }
        categoryRepository.deleteById(id);
    }
}
