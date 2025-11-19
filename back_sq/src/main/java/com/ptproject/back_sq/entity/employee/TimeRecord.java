package com.ptproject.back_sq.entity.employee;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "time_record")
public class TimeRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "clock_in", nullable = false)
    private LocalDateTime clockIn;

    @Column(name = "clock_out")
    private LocalDateTime clockOut;

    public TimeRecord(Employee employee){
        this.employee = employee;
        this.clockIn = LocalDateTime.now();
    }

    public void clockOut(){
        this.clockOut = LocalDateTime.now();
    }

    public LocalDateTime getClockOut() {
        return clockOut;
    }
}
