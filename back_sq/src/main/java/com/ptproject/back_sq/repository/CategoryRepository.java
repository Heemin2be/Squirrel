package com.ptproject.back_sq.repository;


import com.ptproject.back_sq.entity.menu.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {
}
