package com.ptproject.back_sq.dto.menu;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MenuRequest {

    private String name;
    private int price;
    private int cost;
    private String imageUrl;
    private Long categoryId;  // 어떤 카테고리에 속하는지
}
