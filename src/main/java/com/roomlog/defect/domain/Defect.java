package com.roomlog.defect.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "defect")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Defect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "defect_id")
    private Long id;

    @Column(name = "analysis_id", nullable = false)
    private Long analysisId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String severity;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Float area;

    @Column(name = "estimated_cost", nullable = false)
    private Integer estimatedCost;

    @Column(name = "before_image_url")
    private String beforeImageUrl;

    @Column(name = "after_image_url")
    private String afterImageUrl;

    @Column
    private String description;

    @Column
    private Float x;

    @Column
    private Float y;

    @Column
    private Float z;

    @Builder
    public Defect(Long analysisId, String type, String severity, String location, Float area,
                  Integer estimatedCost, String beforeImageUrl, String afterImageUrl,
                  String description, Float x, Float y, Float z) {
        this.analysisId = analysisId;
        this.type = type;
        this.severity = severity;
        this.location = location;
        this.area = area;
        this.estimatedCost = estimatedCost;
        this.beforeImageUrl = beforeImageUrl;
        this.afterImageUrl = afterImageUrl;
        this.description = description;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
