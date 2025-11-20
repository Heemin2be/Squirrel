package com.ptproject.back_sq.entity.employee;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

//주석
@Entity
@Getter
@NoArgsConstructor
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 100)
    private String pin;

    @Column(nullable = false, precision = 10, scale = 0)
    private BigDecimal hourlyWage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmployeeRole role;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeRecord> timeRecords = new ArrayList<>();

    public Employee(String name,String encodedPin, BigDecimal hourlyWage, EmployeeRole role){
        this.name = name;
        this.pin = encodedPin;
        this.hourlyWage = hourlyWage;
        this.role = role;
    }

    public void changeHourlyWage(BigDecimal hourlyWage){
        this.hourlyWage = hourlyWage;
    }

}
