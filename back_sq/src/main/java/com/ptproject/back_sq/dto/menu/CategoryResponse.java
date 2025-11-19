package com.ptproject.back_sq.dto.menu;

import com.ptproject.back_sq.entity.menu.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;

    public static CategoryResponse from (Category category){
        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }
}
