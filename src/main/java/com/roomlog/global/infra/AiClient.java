package com.roomlog.global.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomlog.analysis.dto.AiCompareRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AiClient {

    private static final String API_KEY_HEADER = "X-Api-Key";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${ai.server-url}")
    private String aiServerUrl;

    @Value("${ai.api-key}")
    private String aiApiKey;

    @Value("${ai.callback-base-url}")
    private String callbackBaseUrl;

    public String analysisCallbackUrl(Long analysisId) {
        return callbackBaseUrl + "/analyses/" + analysisId + "/result";
    }

    public String scanCallbackUrl(Long scanId) {
        return callbackBaseUrl + "/scans/" + scanId + "/result";
    }

    public void requestReconstruction(Long scanId, byte[] fileBytes, String filename, String callbackUrl) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("scan_id", String.valueOf(scanId));
        body.add("callback_url", callbackUrl);
        body.add("file", namedByteArrayResource(fileBytes, filename));

        restTemplate.postForObject(aiServerUrl + "/reconstruction", multipartEntity(body), Void.class);
    }

    public void requestDefectDetection(Long analysisId, Long scanId, byte[] fileBytes, String filename, String callbackUrl) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("analysis_id", String.valueOf(analysisId));
        body.add("scan_id", String.valueOf(scanId));
        body.add("callback_url", callbackUrl);
        body.add("file", namedByteArrayResource(fileBytes, filename));

        restTemplate.postForObject(aiServerUrl + "/defect-detection", multipartEntity(body), Void.class);
    }

    public void requestDefectComparison(Long analysisId,
                                        Long inScanId, byte[] inFileBytes, String inFilename,
                                        List<AiCompareRequest.DefectItem> inDefects,
                                        Long outScanId, byte[] outFileBytes, String outFilename,
                                        List<AiCompareRequest.DefectItem> outDefects,
                                        String callbackUrl) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("analysis_id", String.valueOf(analysisId));
            body.add("in_scan_id", String.valueOf(inScanId));
            body.add("out_scan_id", String.valueOf(outScanId));
            body.add("callback_url", callbackUrl);
            body.add("in_defects_json", objectMapper.writeValueAsString(inDefects));
            body.add("out_defects_json", objectMapper.writeValueAsString(outDefects));
            body.add("in_file", namedByteArrayResource(inFileBytes, inFilename));
            body.add("out_file", namedByteArrayResource(outFileBytes, outFilename));

            restTemplate.postForObject(aiServerUrl + "/defect-comparison", multipartEntity(body), Void.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("하자 정보 직렬화 중 오류가 발생했습니다.", e);
        }
    }

    private ByteArrayResource namedByteArrayResource(byte[] bytes, String filename) {
        return new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        };
    }

    private HttpEntity<MultiValueMap<String, Object>> multipartEntity(MultiValueMap<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set(API_KEY_HEADER, aiApiKey);
        return new HttpEntity<>(body, headers);
    }
}
