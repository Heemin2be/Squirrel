package com.ptproject.back_sq.config;

import com.ptproject.back_sq.entity.employee.Employee;
import com.ptproject.back_sq.entity.employee.EmployeeRole;
import com.ptproject.back_sq.entity.menu.Category;
import com.ptproject.back_sq.entity.menu.Menu;
import com.ptproject.back_sq.entity.order.StoreTable;
import com.ptproject.back_sq.repository.CategoryRepository;
import com.ptproject.back_sq.repository.EmployeeRepository;
import com.ptproject.back_sq.repository.MenuRepository;
import com.ptproject.back_sq.repository.StoreTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;
    private final EmployeeRepository employeeRepository;
    private final CategoryRepository categoryRepository;
    private final MenuRepository menuRepository;
    private final StoreTableRepository storeTableRepository;

    @Bean
    @Transactional
    public CommandLineRunner initDatabase() {
        return args -> {
            // Run initializer only if the database is empty
            if (categoryRepository.count() > 0 || employeeRepository.count() > 0) {
                return;
            }

            // == Employees ==
            Employee admin = new Employee("관리자", passwordEncoder.encode("0000"), BigDecimal.ZERO, EmployeeRole.ROLE_ADMIN);
            Employee staff = new Employee("직원1", passwordEncoder.encode("1234"), BigDecimal.valueOf(12000), EmployeeRole.ROLE_STAFF);
            employeeRepository.saveAll(List.of(admin, staff));

            // == Categories ==
            Category category1 = new Category("메인 요리");
            Category category2 = new Category("사이드");
            Category category3 = new Category("음료");
            categoryRepository.saveAll(List.of(category1, category2, category3));

            // == Menus ==
            // 토속전 (Sold Out)
            Menu menu1 = new Menu("토속전", 5000, 2000, "/assets/tosokjeon.png", category1);
            menu1.changeSoldOut(true);

            // 임자탕 (Sold Out)
            Menu menu2 = new Menu("임자탕", 9000, 4000, "/assets/imjatang.jpeg", category1);
            menu2.changeSoldOut(true);

            // 비빔밥
            Menu menu3 = new Menu("비빔밥", 8000, 3500, "/assets/bibimbap.jpeg", category1);

            // 샐러드
            Menu menu4 = new Menu("샐러드", 6000, 2500, "/assets/salad.png", category1);

            // 도토리파전
            Menu menu5 = new Menu("도토리파전", 12000, 5000, "/assets/pajeon.jpeg", category1);
            
            // 묵밥
            Menu menu6 = new Menu("묵밥", 8000, 3000, "/assets/mukbap.png", category1);

            // 묵보쌈
            Menu menu7 = new Menu("묵보쌈", 30000, 15000, "/assets/mukbossam.png", category1);

            // 계란찜
            Menu menu8 = new Menu("계란찜", 3000, 1000, null, category2);

            // 공기밥
            Menu menu9 = new Menu("공기밥", 1000, 300, null, category2);

            // 콜라
            Menu menu10 = new Menu("콜라", 2000, 500, null, category3);

            // 사이다
            Menu menu11 = new Menu("사이다", 2000, 500, null, category3);

            menuRepository.saveAll(List.of(menu1, menu2, menu3, menu4, menu5, menu6, menu7, menu8, menu9, menu10, menu11));

            // == Tables ==
            for (int i = 1; i <= 8; i++) {
                storeTableRepository.save(new StoreTable(i));
            }
        };
    }
}
