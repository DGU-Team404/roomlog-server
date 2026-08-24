package com.roomlog.defect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.defect.domain.DefectRepairGuide;
import lombok.Getter;

@Getter
public class RepairVideo {

    private final String title;

    private final String url;

    @JsonProperty("thumbnail_url")
    private final String thumbnailUrl;

    private final String channel;

    private RepairVideo(String title, String url, String thumbnailUrl, String channel) {
        this.title = title;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.channel = channel;
    }

    /** 영상 링크가 없으면 null을 반환해 응답에서 통째로 빠지게 한다. */
    public static RepairVideo from(DefectRepairGuide guide) {
        if (guide.getVideoUrl() == null || guide.getVideoUrl().isBlank()) return null;
        return new RepairVideo(
                guide.getVideoTitle(), guide.getVideoUrl(),
                guide.getVideoThumbnailUrl(), guide.getVideoChannel());
    }
}
