package com.roomlog.estimate.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@SQLRestriction("is_deleted = false")
@Entity
@Table(name = "estimate")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Estimate {

    public enum Status { REQUESTED, SENT, FAILED, COMPLETED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "estimate_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "analysis_id", nullable = false)
    private Long analysisId;

    @Column(name = "provider_name", nullable = false)
    private String providerName;

    @Column(name = "provider_phone")
    private String providerPhone;

    @Column(name = "provider_address")
    private String providerAddress;

    @Column(name = "provider_rating")
    private Float providerRating;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private String message;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    @Builder
    public Estimate(Long userId, Long roomId, Long analysisId, String providerName,
                    String providerPhone, String providerAddress, Float providerRating, String message) {
        this.userId = userId;
        this.roomId = roomId;
        this.analysisId = analysisId;
        this.providerName = providerName;
        this.providerPhone = providerPhone;
        this.providerAddress = providerAddress;
        this.providerRating = providerRating;
        this.message = message;
        this.status = Status.REQUESTED;
    }
}
