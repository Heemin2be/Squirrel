package com.ptproject.back_sq.entity.menu;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menu")
@Getter
@NoArgsConstructor
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;          // 메뉴명

    @Column(nullable = false)
    private int price;            // 판매가

    @Column(nullable = false)
    private int cost;             // 원가

    @Column(nullable = false)
    private boolean isSoldOut = false; // 품절 여부

    private String imageUrl;      // 이미지 경로 (null 허용)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;    // 카테고리 FK

    public Menu(String name, int price, int cost, String imageUrl, Category category) {
        this.name = name;
        this.price = price;
        this.cost = cost;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public void changeBasicInfo(String name, int price, int cost, String imageUrl, Category category) {
        this.name = name;
        this.price = price;
        this.cost = cost;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public void changeSoldOut(boolean soldOut) {
        this.isSoldOut = soldOut;
    }
}
