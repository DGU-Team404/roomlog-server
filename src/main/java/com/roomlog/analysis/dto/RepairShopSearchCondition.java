package com.roomlog.analysis.dto;

import lombok.Getter;

@Getter
public class RepairShopSearchCondition {

    private final String type;
    private final Integer radius;
    private final String sort;

    public RepairShopSearchCondition(String type, Integer radius, String sort) {
        this.type = type;
        this.radius = radius;
        this.sort = sort;
    }
}
