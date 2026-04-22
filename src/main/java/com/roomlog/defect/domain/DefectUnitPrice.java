package com.roomlog.defect.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "defect_unit_price")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DefectUnitPrice {

    @Id
    @Column(name = "defect_type", length = 20)
    private String defectType;

    @Column(name = "unit_price", nullable = false)
    private Integer unitPrice;

    @Builder
    public DefectUnitPrice(String defectType, Integer unitPrice) {
        this.defectType = defectType;
        this.unitPrice = unitPrice;
    }
}