package com.roomlog.repair.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "repair")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Repair {

    public enum Status { IN_PROGRESS, COMPLETED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "repair_id")
    private Long id;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "estimate_id")
    private Long estimateId;

    @Column(name = "provider_name", nullable = false)
    private String providerName;

    @Column(name = "repair_cost", nullable = false)
    private Integer repairCost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "repaired_at")
    private LocalDateTime repairedAt;

    @Column
    private String note;

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
    public Repair(Long roomId, Long estimateId, String providerName, Integer repairCost, String note) {
        this.roomId = roomId;
        this.estimateId = estimateId;
        this.providerName = providerName;
        this.repairCost = repairCost;
        this.note = note;
        this.status = Status.IN_PROGRESS;
    }

    public void complete() {
        this.status = Status.COMPLETED;
        this.repairedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
