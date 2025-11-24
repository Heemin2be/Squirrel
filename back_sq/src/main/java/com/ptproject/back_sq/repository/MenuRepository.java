package com.ptproject.back_sq.repository;

import com.ptproject.back_sq.entity.menu.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu,Long> {
    List<Menu> findBySoldOutFalse();

    List<Menu> findByCategoryId(Long categoryId);

    List<Menu> findByCategoryIdAndSoldOutFalse(Long categoryId);

    boolean existsByCategoryId(Long categoryId);
}
