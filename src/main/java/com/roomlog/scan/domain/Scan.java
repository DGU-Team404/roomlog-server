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
    public enum ScanType { IN, OUT }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scan_id")
    private Long id;

    @Column(name = "room_id", unique = true)
    private Long roomId;

    @Column(name = "file_url")
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "scan_type", nullable = false)
    private ScanType scanType;

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
    public Scan(Long roomId, String fileUrl, Status status, String thumbnailUrl, ScanType scanType) {
        this.roomId = roomId;
        this.fileUrl = fileUrl;
        this.status = status;
        this.thumbnailUrl = thumbnailUrl;
        this.scanType = scanType;
    }

    public void assignRoom(Long roomId) {
        this.roomId = roomId;
    }

    public void updateFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void complete(String fileUrl, String thumbnailUrl) {
        this.fileUrl = fileUrl;
        this.thumbnailUrl = thumbnailUrl;
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
