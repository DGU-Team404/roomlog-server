package com.roomlog.house.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@SQLRestriction("is_deleted = false")
@Entity
@Table(name = "house")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class House {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "house_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column
    private String address;

    @Column(name = "house_color", length = 30)
    private String houseColor;

    @Column(name = "floor_color", length = 30)
    private String floorColor;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public House(Long userId, String name, String address, String houseColor, String floorColor) {
        this.userId = userId;
        this.name = name;
        this.address = address;
        this.houseColor = houseColor;
        this.floorColor = floorColor;
    }

    public void update(String name, String address, String houseColor, String floorColor) {
        this.name = name;
        this.address = address;
        if (houseColor != null) {
            this.houseColor = houseColor;
        }
        if (floorColor != null) {
            this.floorColor = floorColor;
        }
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
