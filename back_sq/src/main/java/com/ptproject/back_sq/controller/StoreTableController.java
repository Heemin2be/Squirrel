package com.ptproject.back_sq.controller;

import com.ptproject.back_sq.entity.order.StoreTable;
import com.ptproject.back_sq.service.StoreTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tables")
@CrossOrigin(origins = "*")
public class StoreTableController {

    private final StoreTableService storeTableService;

    @GetMapping
    public List<StoreTable> getAllTables() {
        return storeTableService.getAllTables();
    }

    @GetMapping("/{id}")
    public StoreTable getTableById(@PathVariable Long id) {
        return storeTableService.getTableById(id);
    }
}
