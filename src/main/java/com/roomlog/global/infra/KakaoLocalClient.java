package com.roomlog.global.infra;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KakaoLocalClient {

    private static final String ADDRESS_SEARCH_URL = "https://dapi.kakao.com/v2/local/search/address.json";
    private static final String KEYWORD_SEARCH_URL = "https://dapi.kakao.com/v2/local/search/keyword.json";

    private final RestTemplate restTemplate;

    @Value("${kakao.api-key}")
    private String apiKey;

    public double[] geocodeAddress(String address) {
        String url = UriComponentsBuilder.fromHttpUrl(ADDRESS_SEARCH_URL)
                .queryParam("query", address)
                .build().toUriString();

        ResponseEntity<AddressSearchResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, authHeader(), AddressSearchResponse.class);

        List<AddressDocument> documents = response.getBody().getDocuments();
        if (documents == null || documents.isEmpty()) {
            return null;
        }
        AddressDocument doc = documents.get(0);
        return new double[]{Double.parseDouble(doc.getY()), Double.parseDouble(doc.getX())};
    }

    public List<KakaoPlace> searchKeyword(String keyword, double lat, double lng, int radiusMeters, String sort) {
        String url = UriComponentsBuilder.fromHttpUrl(KEYWORD_SEARCH_URL)
                .queryParam("query", keyword)
                .queryParam("x", lng)
                .queryParam("y", lat)
                .queryParam("radius", Math.min(radiusMeters, 20000))
                .queryParam("sort", sort != null ? sort : "distance")
                .build().toUriString();

        ResponseEntity<KeywordSearchResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, authHeader(), KeywordSearchResponse.class);

        return response.getBody().getDocuments();
    }

    public KakaoPlace getPlaceById(String placeId) {
        String url = UriComponentsBuilder.fromHttpUrl("https://dapi.kakao.com/v2/local/place/id.json")
                .queryParam("id", placeId)
                .build().toUriString();

        ResponseEntity<KeywordSearchResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, authHeader(), KeywordSearchResponse.class);

        List<KakaoPlace> documents = response.getBody().getDocuments();
        if (documents == null || documents.isEmpty()) return null;
        return documents.get(0);
    }

    private HttpEntity<Void> authHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);
        return new HttpEntity<>(headers);
    }

    @Getter
    public static class AddressSearchResponse {
        private List<AddressDocument> documents;
    }

    @Getter
    public static class AddressDocument {
        private String x;
        private String y;
    }

    @Getter
    public static class KeywordSearchResponse {
        private List<KakaoPlace> documents;
    }

    @Getter
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
