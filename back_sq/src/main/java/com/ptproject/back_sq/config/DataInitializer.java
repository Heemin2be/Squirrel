package com.ptproject.back_sq.config;

import com.ptproject.back_sq.entity.employee.Employee;
import com.ptproject.back_sq.entity.employee.EmployeeRole;
import com.ptproject.back_sq.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initEmployees(EmployeeRepository employeeRepository) {
        return args -> {
            if (employeeRepository.count() == 0) {
                Employee admin = new Employee(
                        "관리자",
                        passwordEncoder.encode("0000"),   // 관리자 PIN
                        BigDecimal.valueOf(0),
                        EmployeeRole.ROLE_ADMIN
                );

                Employee staff = new Employee(
                        "직원1",
                        passwordEncoder.encode("1234"),   // 직원 PIN
                        BigDecimal.valueOf(12000),
                        EmployeeRole.ROLE_STAFF
                );

                employeeRepository.save(admin);
                employeeRepository.save(staff);
            }
        };
    }
}
