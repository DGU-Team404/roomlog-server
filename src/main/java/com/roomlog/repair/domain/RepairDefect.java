package com.roomlog.repair.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "repair_defect",
        uniqueConstraints = @UniqueConstraint(columnNames = {"repair_id", "defect_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RepairDefect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "repair_defect_id")
    private Long id;

    @Column(name = "repair_id", nullable = false)
    private Long repairId;

    @Column(name = "defect_id", nullable = false)
    private Long defectId;

    @Builder
    public RepairDefect(Long repairId, Long defectId) {
        this.repairId = repairId;
        this.defectId = defectId;
    }
}
