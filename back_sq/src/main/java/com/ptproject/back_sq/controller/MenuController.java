package com.ptproject.back_sq.controller;

import com.ptproject.back_sq.dto.menu.MenuResponse;
import com.ptproject.back_sq.entity.menu.Menu;
import com.ptproject.back_sq.service.MenuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@CrossOrigin(origins = "*")
public class MenuController {
    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    // 전체 메뉴 조회
    @GetMapping
    public List<MenuResponse> getAllMenus() {
        return menuService.getAllMenus().stream()
                .map(MenuResponse::from)
                .toList();
    }

    // 품절되지 않은 메뉴만 조회 (키오스크용)
    @GetMapping("/available")
    public List<MenuResponse> getAvailableMenus() {
        return menuService.getAvailableMenus().stream()
                .map(MenuResponse::from)
                .toList();
    }

    // 메뉴 생성 (테스트용, 나중에 관리자 화면에서 호출)
    @PostMapping
    public Menu createMenu(@RequestParam String name,
                           @RequestParam int price,
                           @RequestParam Long categoryId) {
        return menuService.createMenu(name, price, categoryId);
    }

    // 품절 상태 변경
    @PatchMapping("/{id}/sold-out")
    public Menu changeSoldOut(@PathVariable Long id,
                              @RequestParam boolean soldOut) {
        return menuService.changeSoldOut(id, soldOut);
    }

}
