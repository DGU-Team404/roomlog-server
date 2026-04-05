package com.roomlog.analysis.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "analysis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Analysis {

    public enum Status { PENDING, COMPLETED, FAILED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_id")
    private Long id;

    @Column(name = "in_room_id", nullable = false)
    private Long inRoomId;

    @Column(name = "out_room_id", nullable = false)
    private Long outRoomId;

    @Column(name = "in_scan_id", nullable = false)
    private Long inScanId;

    @Column(name = "out_scan_id", nullable = false)
    private Long outScanId;

    @Column(name = "total_cost")
    private Integer totalCost;

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
    public Analysis(Long inRoomId, Long outRoomId, Long inScanId, Long outScanId) {
        this.inRoomId = inRoomId;
        this.outRoomId = outRoomId;
        this.inScanId = inScanId;
        this.outScanId = outScanId;
        this.status = Status.PENDING;
    }

    public void complete(Integer totalCost) {
        this.totalCost = totalCost;
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
