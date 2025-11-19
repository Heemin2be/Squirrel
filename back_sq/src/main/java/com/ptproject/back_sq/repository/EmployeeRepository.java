package com.ptproject.back_sq.repository;

import com.ptproject.back_sq.entity.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}