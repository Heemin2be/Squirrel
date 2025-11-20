package com.ptproject.back_sq.dto.websocket;

import com.ptproject.back_sq.entity.menu.Menu;
import lombok.Data;

@Data
public class MenuUpdatePayload {
    private Long id;
    private String name;
    private int price;
    private int cost;
    private String imageUrl;
    private boolean soldOut;
    private Long categoryId;

    public static MenuUpdatePayload from(Menu menu) {
        MenuUpdatePayload p = new MenuUpdatePayload();
        p.setId(menu.getId());
        p.setName(menu.getName());
        p.setPrice(menu.getPrice());
        p.setCost(menu.getCost());
        p.setImageUrl(menu.getImageUrl());
        p.setSoldOut(menu.isSoldOut());
        p.setCategoryId(menu.getCategory().getId());
        return p;
    }
}
