package com.ptproject.back_sq.service;

import com.ptproject.back_sq.entity.order.StoreTable;
import com.ptproject.back_sq.repository.StoreTableRepository;
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
}
