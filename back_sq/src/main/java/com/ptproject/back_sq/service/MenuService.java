package com.ptproject.back_sq.service;

import com.ptproject.back_sq.dto.menu.MenuRequest;
import com.ptproject.back_sq.dto.menu.MenuResponse;
import com.ptproject.back_sq.dto.menu.SoldOutRequest;
import com.ptproject.back_sq.dto.websocket.MenuDeletePayload;
import com.ptproject.back_sq.dto.websocket.MenuUpdatePayload;
import com.ptproject.back_sq.dto.websocket.WebSocketMessage;
import com.ptproject.back_sq.entity.menu.Category;
import com.ptproject.back_sq.entity.menu.Menu;
import com.ptproject.back_sq.repository.CategoryRepository;
import com.ptproject.back_sq.repository.MenuRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 전체 메뉴 조회 (POS용)
    @Transactional(readOnly = true)
    public List<MenuResponse> getAllMenus() {
        return menuRepository.findAll().stream()
                .map(MenuResponse::from)
                .toList();
    }

    // 품절 아닌 메뉴만 조회 (Kiosk용)
    @Transactional(readOnly = true)
    public List<MenuResponse> getAvailableMenus() {
        return menuRepository.findBySoldOutFalse().stream()
                .map(MenuResponse::from)
                .toList();
    }

    // 단건 조회
    @Transactional(readOnly = true)
    public MenuResponse getMenu(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Menu not found: " + id));
        return MenuResponse.from(menu);
    }

    // 메뉴 생성
    public MenuResponse createMenu(MenuRequest request) {
        Category category = findCategory(request.getCategoryId());

        Menu menu = new Menu(
                request.getName(),
                request.getPrice(),
                request.getCost(),
                request.getImageUrl(),
                category
        );

        Menu saved = menuRepository.save(menu);

        // 👉 WebSocket: 메뉴 생성 알림
        MenuUpdatePayload payload = MenuUpdatePayload.from(saved);
        WebSocketMessage<MenuUpdatePayload> msg =
                new WebSocketMessage<>("menu-created", payload);
        messagingTemplate.convertAndSend("/topic/menu-update", msg);

        return MenuResponse.from(saved);
    }

    // 메뉴 수정
    public MenuResponse updateMenu(Long id, MenuRequest request) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Menu not found: " + id));

        Category category = findCategory(request.getCategoryId());

        menu.changeBasicInfo(
                request.getName(),
                request.getPrice(),
                request.getCost(),
                request.getImageUrl(),
                category
        );

        // 👉 WebSocket: 메뉴 수정 알림
        MenuUpdatePayload payload = MenuUpdatePayload.from(menu);
        WebSocketMessage<MenuUpdatePayload> msg =
                new WebSocketMessage<>("menu-updated", payload);
        messagingTemplate.convertAndSend("/topic/menu-update", msg);

        return MenuResponse.from(menu);
    }

    // 품절 상태 변경
    public MenuResponse changeSoldOut(Long id, SoldOutRequest request) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Menu not found: " + id));

        menu.changeSoldOut(request.isSoldOut());

        // 👉 WebSocket: 품절 변경 알림
        MenuUpdatePayload payload = MenuUpdatePayload.from(menu);
        WebSocketMessage<MenuUpdatePayload> msg =
                new WebSocketMessage<>("menu-soldout-changed", payload);
        messagingTemplate.convertAndSend("/topic/menu-update", msg);

        return MenuResponse.from(menu);
    }

    // 삭제
    public void deleteMenu(Long id) {
        if (!menuRepository.existsById(id)) {
            throw new EntityNotFoundException("Menu not found: " + id);
        }
        menuRepository.deleteById(id);

        // 👉 WebSocket: 메뉴 삭제 알림
        MenuDeletePayload payload = new MenuDeletePayload(id);
        WebSocketMessage<MenuDeletePayload> msg =
                new WebSocketMessage<>("menu-deleted", payload);
        messagingTemplate.convertAndSend("/topic/menu-update", msg);
    }

    // 카테고리 조회 공통 로직
    private Category findCategory(Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("categoryId is required");
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + categoryId));
    }
}
