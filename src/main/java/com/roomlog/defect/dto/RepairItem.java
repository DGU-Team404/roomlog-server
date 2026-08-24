package com.roomlog.defect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RepairItem {

    /** 준비물 이름 */
    private String name;

    /** 최저가(원) */
    private Integer price;

    @JsonProperty("image_url")
    private String imageUrl;

    /** 구매처 링크 */
    private String url;
}
