package com.ptproject.back_sq.repository;

import com.ptproject.back_sq.entity.order.StoreTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreTableRepository extends JpaRepository<StoreTable, Long> {
}
