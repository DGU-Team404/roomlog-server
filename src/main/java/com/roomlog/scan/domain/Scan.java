package com.roomlog.scan.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@SQLRestriction("is_deleted = false")
@Entity
@Table(name = "scan")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scan {

    public enum Status { SCANNING, COMPLETED, FAILED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scan_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "house_id")
    private Long houseId;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "file_url", length = 2000)
    private String fileUrl;

    @Column(name = "mesh_url", length = 2000)
    private String meshUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

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
    public Scan(Long userId, Long houseId, Long roomId, String fileUrl, Status status) {
        this.userId = userId;
        this.houseId = houseId;
        this.roomId = roomId;
        this.fileUrl = fileUrl;
        this.status = status;
    }

    public void assignRoom(Long roomId) {
        this.roomId = roomId;
    }

    public void updateFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void updateMeshUrl(String meshUrl) {
        this.meshUrl = meshUrl;
    }

    public void complete() {
        this.status = Status.COMPLETED;
    }

    public void fail() {
        this.status = Status.FAILED;
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
