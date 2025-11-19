package com.ptproject.back_sq.entity.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "store_table")
public class StoreTable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int tableNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TableStatus status = TableStatus.EMPTY;

    public StoreTable(int tableNumber)
    {
        this.tableNumber = tableNumber;
        this.status = TableStatus.EMPTY;
    }

    public void occupy(){
        this.status = TableStatus.OCCUPIED;
    }

    public void empty(){
        this.status = TableStatus.EMPTY;
    }
}
