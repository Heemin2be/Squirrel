package com.ptproject.back_sq.controller;

import com.ptproject.back_sq.dto.menu.MenuRequest;
import com.ptproject.back_sq.dto.menu.MenuResponse;
import com.ptproject.back_sq.dto.menu.SoldOutRequest;
import com.ptproject.back_sq.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    // 전체 메뉴 조회 (POS + 테스트용) - 카테고리 필터 지원
    @GetMapping
    public List<MenuResponse> getAllMenus(@RequestParam(required = false) Long categoryId) {
        return menuService.getMenus(categoryId);
    }

    // 품절 아닌 메뉴만 조회 (Kiosk용)
    @GetMapping("/available")
    public List<MenuResponse> getAvailableMenus(@RequestParam(required = false) Long categoryId) {
        if (categoryId == null) {
            return menuService.getAvailableMenus();
        }
        return menuService.getMenus(categoryId).stream()
                .filter(menu -> !menu.isSoldOut())
                .toList();
    }

    // 단건 조회
    @GetMapping("/{id}")
    public MenuResponse getMenu(@PathVariable Long id) {
        return menuService.getMenu(id);
    }

    // 🔹 메뉴 생성 (POS - 관리자용)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public MenuResponse createMenu(@RequestBody MenuRequest request) {
        return menuService.createMenu(request);
    }

    // 🔹 메뉴 수정
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public MenuResponse updateMenu(
            @PathVariable Long id,
            @RequestBody MenuRequest request
    ) {
        return menuService.updateMenu(id, request);
    }

    // 🔹 품절 상태 변경 (필요 시 다른 필드도 확장 가능)
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public MenuResponse changeSoldOut(
            @PathVariable Long id,
            @RequestBody SoldOutRequest request
    ) {
        return menuService.changeSoldOut(id, request);
    }

    // 🔹 메뉴 삭제
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
    }
}
