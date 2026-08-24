package com.roomlog.global.infra;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import java.util.List;

/**
 * 유튜브 검색. 검색어로 수리 방법 영상 한 건의 제목·썸네일·채널을 가져온다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class YoutubeClient {

    private static final String SEARCH_URL = "https://www.googleapis.com/youtube/v3/search";
    private static final String WATCH_URL = "https://www.youtube.com/watch?v=";

    /**
     * 1건만 요청하면 결과가 있는데도 빈 페이지가 오는 경우가 있다.
     * (재생 불가·지역 제한 영상이 페이징 후에 걸러지기 때문)
     * 여유 있게 받아 첫 번째 유효한 영상을 쓴다. 검색 호출 비용은 개수와 무관하게 동일하다.
     */
    private static final int SEARCH_SIZE = 10;

    private final RestTemplate restTemplate;

    @Value("${youtube.api-key}")
    private String apiKey;

    /** 검색 결과가 없거나 API 오류면 빈 목록. 영상 없이도 안내는 만들어져야 하므로 예외를 던지지 않는다. */
    public List<Video> search(String keyword, int count) {
        if (keyword == null || keyword.isBlank()) return List.of();

        // 인코딩한 문자열을 넘기면 RestTemplate이 한 번 더 인코딩하므로 URI 객체로 넘긴다.
        URI uri = UriComponentsBuilder.fromHttpUrl(SEARCH_URL)
                .queryParam("part", "snippet")
                .queryParam("q", keyword)
                .queryParam("type", "video")
                .queryParam("maxResults", SEARCH_SIZE)
                .queryParam("regionCode", "KR")
                .queryParam("relevanceLanguage", "ko")
                .queryParam("key", apiKey)
                .build().encode().toUri();

        try {
            SearchResponse response = restTemplate.getForObject(uri, SearchResponse.class);
            List<SearchItem> items = response != null ? response.getItems() : null;
            if (items == null || items.isEmpty()) return List.of();

            return items.stream()
                    .filter(item -> item.getId() != null && item.getId().getVideoId() != null)
                    .limit(count)
                    .map(this::toVideo)
                    .toList();
        } catch (RestClientException e) {
            log.warn("YouTube search failed. keyword={}, reason={}", keyword, e.getMessage());
            return List.of();
        }
    }

    private Video toVideo(SearchItem item) {
        Snippet snippet = item.getSnippet();
        return new Video(
                HtmlText.clean(snippet != null ? snippet.getTitle() : null),
                WATCH_URL + item.getId().getVideoId(),
                thumbnailUrl(snippet),
                snippet != null ? snippet.getChannelTitle() : null);
    }

    private String thumbnailUrl(Snippet snippet) {
        if (snippet == null || snippet.getThumbnails() == null) return null;
        Thumbnail medium = snippet.getThumbnails().getMedium();
        return medium != null ? medium.getUrl() : null;
    }

    public record Video(String title, String url, String thumbnailUrl, String channel) {
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchResponse {
        private List<SearchItem> items;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchItem {
        private VideoId id;
        private Snippet snippet;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VideoId {
        private String videoId;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Snippet {
        private String title;
        private String channelTitle;
        private Thumbnails thumbnails;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Thumbnails {
        private Thumbnail medium;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Thumbnail {
        private String url;
    }
}
