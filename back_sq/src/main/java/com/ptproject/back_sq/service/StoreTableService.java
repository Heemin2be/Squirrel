package com.ptproject.back_sq.service;

import com.ptproject.back_sq.entity.order.StoreTable;
import com.ptproject.back_sq.repository.StoreTableRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreTableService {
    private final StoreTableRepository storeTableRepository;

    public List<StoreTable> getAllTables(){
        return storeTableRepository.findAll();
    }

    public StoreTable getTableById(Long id) {
        return storeTableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Table not found with id: " + id));
    }
}
