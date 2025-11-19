package com.ptproject.back_sq.service;

import com.ptproject.back_sq.entity.menu.Category;
import com.ptproject.back_sq.entity.menu.Menu;
import com.ptproject.back_sq.repository.CategoryRepository;
import com.ptproject.back_sq.repository.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;

    public MenuService(MenuRepository menuRepository, CategoryRepository categoryRepository) {
        this.menuRepository = menuRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    public List<Menu> getAvailableMenus() {
        return menuRepository.findBySoldOutFalse();
    }

    public Menu createMenu(String name, int price, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리"));
        Menu menu = new Menu(name, price, category);
        return menuRepository.save(menu);
    }

    public Menu changeSoldOut(Long menuId, boolean soldOut) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴"));
        menu.changeSoldOut(soldOut);
        return menu;
    }
}
