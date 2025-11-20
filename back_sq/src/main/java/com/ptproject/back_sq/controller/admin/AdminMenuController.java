package com.ptproject.back_sq.controller.admin;

import com.ptproject.back_sq.dto.menu.MenuRequest;
import com.ptproject.back_sq.dto.menu.MenuResponse;
import com.ptproject.back_sq.dto.menu.SoldOutRequest;
import com.ptproject.back_sq.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/menus")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminMenuController {

    private final MenuService menuService;

    // (선택) 관리자용 전체 메뉴 조회 - DTO로 보고 싶을 때 사용
    @GetMapping
    public List<MenuResponse> getAllMenus() {
        return menuService.getAllMenus();
    }

    // 메뉴 단건 조회 (관리자 화면에서 수정 모달 띄울 때 등)
    @GetMapping("/{id}")
    public MenuResponse getMenu(@PathVariable Long id) {
        return menuService.getMenu(id);
    }

    // 메뉴 생성
    @PostMapping
    public MenuResponse createMenu(@RequestBody MenuRequest request) {
        return menuService.createMenu(request);
    }

    // 메뉴 수정
    @PutMapping("/{id}")
    public MenuResponse updateMenu(@PathVariable Long id,
                                   @RequestBody MenuRequest request) {
        return menuService.updateMenu(id, request);
    }

    // 품절 상태 변경
    @PatchMapping("/{id}/sold-out")
    public MenuResponse changeSoldOut(@PathVariable Long id,
                                      @RequestBody SoldOutRequest request) {
        return menuService.changeSoldOut(id, request);
    }

    // 메뉴 삭제
    @DeleteMapping("/{id}")
    public void deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
    }
}
