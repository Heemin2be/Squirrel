package com.ptproject.back_sq.dto.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ptproject.back_sq.entity.menu.Menu;
import lombok.Data;

@Data
public class MenuUpdatePayload {
    private Long menuId;
    private String name;
    private int price;
    private int cost;
    private String imageUrl;
    @JsonProperty("isSoldOut")
    private boolean soldOut;
    private Long categoryId;
    private boolean deleted;

    public static MenuUpdatePayload from(Menu menu) {
        MenuUpdatePayload p = new MenuUpdatePayload();
        p.setMenuId(menu.getId());
        p.setName(menu.getName());
        p.setPrice(menu.getPrice());
        p.setCost(menu.getCost());
        p.setImageUrl(menu.getImageUrl());
        p.setSoldOut(menu.isSoldOut());
        p.setCategoryId(menu.getCategory() != null ? menu.getCategory().getId() : null);
        p.setDeleted(false);
        return p;
    }

    public static MenuUpdatePayload deleted(Long menuId) {
        MenuUpdatePayload payload = new MenuUpdatePayload();
        payload.setMenuId(menuId);
        payload.setDeleted(true);
        return payload;
    }
}
