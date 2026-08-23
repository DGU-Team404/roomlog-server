package com.roomlog.defect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RepairItem {

    private String name;

    @JsonProperty("coupang_url")
    private String coupangUrl;

    @JsonProperty("gmarket_url")
    private String gmarketUrl;

    @JsonProperty("lowest_price")
    private Integer lowestPrice;
}
