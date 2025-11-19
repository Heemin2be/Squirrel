package com.ptproject.back_sq.dto.menu;

import com.ptproject.back_sq.entity.menu.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MenuResponse {

    private Long id;
    private String name;
    private int price;
    private int cost;
    private boolean soldOut;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;

    public static MenuResponse from(Menu menu) {
        return new MenuResponse(
                menu.getId(),
                menu.getName(),
                menu.getPrice(),
                menu.getCost(),
                menu.isSoldOut(),
                menu.getImageUrl(),
                menu.getCategory() != null ? menu.getCategory().getId() : null,
                menu.getCategory() != null ? menu.getCategory().getName() : null
        );
    }
}
