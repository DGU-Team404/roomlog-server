package com.roomlog.global.infra;

import com.roomlog.analysis.dto.AiCompareRequest;
import com.roomlog.analysis.dto.AiDetectionRequest;
import com.roomlog.scan.dto.AiReconstructionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AiClient {

    private static final String API_KEY_HEADER = "X-Api-Key";

    private final RestTemplate restTemplate;

    @Value("${ai.server-url}")
    private String aiServerUrl;

    @Value("${ai.api-key}")
    private String aiApiKey;

    public void requestReconstruction(AiReconstructionRequest request) {
        restTemplate.postForObject(aiServerUrl + "/reconstruction", authEntity(request), Void.class);
    }

    public void requestDefectDetection(AiDetectionRequest request) {
        restTemplate.postForObject(aiServerUrl + "/defect-detection", authEntity(request), Void.class);
    }

    public void requestDefectComparison(AiCompareRequest request) {
        restTemplate.postForObject(aiServerUrl + "/defect-comparison", authEntity(request), Void.class);
    }

    private <T> HttpEntity<T> authEntity(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(API_KEY_HEADER, aiApiKey);
        return new HttpEntity<>(body, headers);
    }
}
