package com.roomlog.defect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RepairVideo {

    private String title;

    /** 영상 재생 링크 (검색 결과 페이지가 아닌 개별 영상) */
    private String url;

    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;

    private String channel;
}
