package com.roomlog.defect.domain;

import com.roomlog.defect.dto.RepairItem;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "defect_repair_guide")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DefectRepairGuide {

    @Id
    @Column(name = "defect_id")
    private Long defectId;

    @Column(name = "self_repair_possible", nullable = false)
    private boolean selfRepairPossible;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "video_url", columnDefinition = "TEXT")
    private String videoUrl;

    @Convert(converter = RepairItemListConverter.class)
    @Column(name = "items", columnDefinition = "TEXT")
    private List<RepairItem> items;

    @Column(name = "total_cost", nullable = false)
    private Integer totalCost;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public DefectRepairGuide(Long defectId, boolean selfRepairPossible, String description,
                             String videoUrl, List<RepairItem> items, Integer totalCost) {
        this.defectId = defectId;
        this.selfRepairPossible = selfRepairPossible;
        this.description = description;
        this.videoUrl = videoUrl;
        this.items = items;
        this.totalCost = totalCost != null ? totalCost : 0;
    }
}
