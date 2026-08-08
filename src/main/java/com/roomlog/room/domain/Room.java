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

    @Column(name = "move_in_date")
    private LocalDate moveInDate;

    @Column(name = "move_out_date")
    private LocalDate moveOutDate;

    @Column(name = "ply_url", length = 2000)
    private String plyUrl;

    @Column(name = "thumbnail_url", length = 2000)
    private String thumbnailUrl;

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
    public Room(Long houseId, String name, LocalDate moveInDate, LocalDate moveOutDate, String plyUrl, String thumbnailUrl) {
        this.houseId = houseId;
        this.name = name;
        this.moveInDate = moveInDate;
        this.moveOutDate = moveOutDate;
        this.plyUrl = plyUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public void update(String name, LocalDate moveInDate, LocalDate moveOutDate) {
        this.name = name;
        this.moveInDate = moveInDate;
        this.moveOutDate = moveOutDate;
    }

    public void updateThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
