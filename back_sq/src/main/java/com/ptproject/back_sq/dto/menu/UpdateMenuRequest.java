package com.ptproject.back_sq.dto.menu;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateMenuRequest {

    private String name;
    private int price;
    private Long categoryId;
}
