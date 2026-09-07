package com.roomlog.global.infra;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoLocalClient {

    private static final String ADDRESS_SEARCH_URL = "https://dapi.kakao.com/v2/local/search/address.json";
    private static final String KEYWORD_SEARCH_URL = "https://dapi.kakao.com/v2/local/search/keyword.json";

    private final RestTemplate restTemplate;

    @Value("${kakao.api-key}")
    private String apiKey;

    public double[] geocodeAddress(String address) {
        if (address == null || address.isBlank()) return null;

        double[] coords = geocodeByAddressSearch(address);
        // 주소 검색 API는 지번/도로명만 인식하므로 건물명이나 상세주소가 붙은 주소는 키워드 검색으로 다시 찾는다.
        return coords != null ? coords : geocodeByKeywordSearch(address);
    }

    private double[] geocodeByAddressSearch(String address) {
        String url = UriComponentsBuilder.fromHttpUrl(ADDRESS_SEARCH_URL)
                .queryParam("query", address)
                .build().toUriString();

        try {
            ResponseEntity<AddressSearchResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, authHeader(), AddressSearchResponse.class);
            List<AddressDocument> documents = documentsOf(response.getBody());
            if (documents == null || documents.isEmpty()) return null;
            AddressDocument doc = documents.get(0);
            return new double[]{Double.parseDouble(doc.getY()), Double.parseDouble(doc.getX())};
        } catch (RestClientException e) {
            log.error("Kakao geocode API error: {}", e.getMessage());
            return null;
        }
    }

    private double[] geocodeByKeywordSearch(String address) {
        String url = UriComponentsBuilder.fromHttpUrl(KEYWORD_SEARCH_URL)
                .queryParam("query", address)
                .build().toUriString();

        try {
            ResponseEntity<KeywordSearchResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, authHeader(), KeywordSearchResponse.class);
            List<KakaoPlace> documents = documentsOf(response.getBody());
            if (documents == null || documents.isEmpty()) return null;
            KakaoPlace place = documents.get(0);
            return new double[]{Double.parseDouble(place.getY()), Double.parseDouble(place.getX())};
        } catch (RestClientException e) {
            log.error("Kakao keyword geocode API error: {}", e.getMessage());
            return null;
        }
    }

    public List<KakaoPlace> searchKeyword(String keyword, double lat, double lng, int radiusMeters, String sort) {
        String url = UriComponentsBuilder.fromHttpUrl(KEYWORD_SEARCH_URL)
                .queryParam("query", keyword)
                .queryParam("x", lng)
                .queryParam("y", lat)
                .queryParam("radius", Math.min(radiusMeters, 20000))
                .queryParam("sort", sort != null ? sort : "distance")
                .build().toUriString();

        try {
            ResponseEntity<KeywordSearchResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, authHeader(), KeywordSearchResponse.class);
            return documentsOf(response.getBody());
        } catch (RestClientException e) {
            log.error("Kakao keyword search API error: {}", e.getMessage());
            return null;
        }
    }

    public KakaoPlace getPlaceById(String placeId) {
        String url = UriComponentsBuilder.fromHttpUrl("https://dapi.kakao.com/v2/local/place/id.json")
                .queryParam("id", placeId)
                .build().toUriString();

        try {
            ResponseEntity<KeywordSearchResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, authHeader(), KeywordSearchResponse.class);
            List<KakaoPlace> documents = documentsOf(response.getBody());
            if (documents == null || documents.isEmpty()) return null;
            return documents.get(0);
        } catch (RestClientException e) {
            log.error("Kakao place by ID API error: {}", e.getMessage());
            return null;
        }
    }

    private List<AddressDocument> documentsOf(AddressSearchResponse body) {
        return body == null ? null : body.getDocuments();
    }

    private List<KakaoPlace> documentsOf(KeywordSearchResponse body) {
        return body == null ? null : body.getDocuments();
    }

    private HttpEntity<Void> authHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);
        return new HttpEntity<>(headers);
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AddressSearchResponse {
        private List<AddressDocument> documents;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AddressDocument {
        private String x;
        private String y;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KeywordSearchResponse {
        private List<KakaoPlace> documents;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoPlace {
        private String id;
        @JsonProperty("place_name")
        private String placeName;
        private String phone;
        @JsonProperty("road_address_name")
        private String roadAddressName;
        @JsonProperty("address_name")
        private String addressName;
        private String x;
        private String y;
        private String distance;
    }
}
