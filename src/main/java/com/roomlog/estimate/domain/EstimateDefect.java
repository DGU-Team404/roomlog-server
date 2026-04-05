package com.roomlog.estimate.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estimate_defect",
        uniqueConstraints = @UniqueConstraint(columnNames = {"estimate_id", "defect_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EstimateDefect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "estimate_defect_id")
    private Long id;

    @Column(name = "estimate_id", nullable = false)
    private Long estimateId;

    @Column(name = "defect_id", nullable = false)
    private Long defectId;

    @Builder
    public EstimateDefect(Long estimateId, Long defectId) {
        this.estimateId = estimateId;
        this.defectId = defectId;
    }
}