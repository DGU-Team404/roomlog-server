package com.roomlog.room.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SQLRestriction("is_deleted = false")
@Entity
@Table(name = "room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @Column(name = "house_id", nullable = false)
    private Long houseId;

    @Column(nullable = false)
    private String name;

    @Column
    private String address;

    @Column(name = "move_in_date")
    private LocalDate moveInDate;

    @Column(name = "move_out_date")
    private LocalDate moveOutDate;

    @Column(name = "file_url")
    private String fileUrl;

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
    public Room(Long houseId, String name, String address, LocalDate moveInDate, LocalDate moveOutDate, String fileUrl) {
        this.houseId = houseId;
        this.name = name;
        this.address = address;
        this.moveInDate = moveInDate;
        this.moveOutDate = moveOutDate;
        this.fileUrl = fileUrl;
    }

    public void update(String name, String address, LocalDate moveInDate, LocalDate moveOutDate) {
        this.name = name;
        this.address = address;
        this.moveInDate = moveInDate;
        this.moveOutDate = moveOutDate;
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
