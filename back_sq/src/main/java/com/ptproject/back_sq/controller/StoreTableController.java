package com.ptproject.back_sq.controller;

import com.ptproject.back_sq.dto.payment.CreatePaymentRequest;
import com.ptproject.back_sq.entity.order.StoreTable;
import com.ptproject.back_sq.service.PaymentService;
import com.ptproject.back_sq.service.StoreTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tables")
@CrossOrigin(origins = "*")
public class StoreTableController {

    private final StoreTableService storeTableService;
    private final PaymentService paymentService;

    @GetMapping
    public List<StoreTable> getAllTables() {
        return storeTableService.getAllTables();
    }

    @GetMapping("/{id}")
    public StoreTable getTableById(@PathVariable Long id) {
        return storeTableService.getTableById(id);
    }

    @PostMapping("/{tableId}/payment")
    public ResponseEntity<Void> createPaymentForTable(
            @PathVariable Long tableId,
            @RequestBody CreatePaymentRequest request
    ) {
        paymentService.createPaymentForTable(tableId, request);
        return ResponseEntity.ok().build();
    }
}
