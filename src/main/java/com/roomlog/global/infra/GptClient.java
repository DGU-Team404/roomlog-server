package com.roomlog.global.infra;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomlog.global.exception.CustomException;
import com.roomlog.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GptClient {

    private static final String CHAT_COMPLETIONS_URL = "https://api.openai.com/v1/chat/completions";

    private static final String SYSTEM_PROMPT = """
            너는 한국의 원룸/자취방 하자 보수 전문가다. 사용자가 하자 정보를 주면 자가 수리 안내문을 만든다.

            [매우 중요]
            - 자가 수리 가능 여부는 이미 결정되어 전달된다. 그 판정을 절대 뒤집지 말고 그대로 따른다.
            - URL은 절대 생성하지 마라. 링크는 서버가 검색어로 직접 만든다. 검색어(한국어)만 반환하라.

            [description 작성 규칙]
            - 반드시 "해당 하자는 ~한 하자로, ~하기에 스스로 수리 가능합니다." 또는
              "해당 하자는 ~한 하자로, ~하기에 스스로 수리 불가능합니다." 형식의 한 문장으로 쓴다.
            - 하자 종류와 이유가 구체적으로 드러나게 쓴다.

            [자가 수리 가능한 경우]
            - video_search_query: 유튜브에서 이 하자의 셀프 보수 영상을 찾을 검색어 (예: "벽지 들뜸 셀프 보수").
            - items: 수리에 필요한 구매 품목. 1~5개. 집에 흔히 있는 물건(가위, 걸레)은 제외한다.
              - name: 사용자에게 보여줄 품목명 (예: "실크벽지 보수용 조각").
              - search_query: 쇼핑몰 검색어. 짧고 일반적인 단어로 (예: "벽지 보수용 조각").
              - estimated_price: 한국 온라인 최저가 기준 예상 가격(원, 정수). 배송비 제외.

            [자가 수리 불가능한 경우]
            - video_search_query: 빈 문자열.
            - items: 빈 배열.
            """;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gpt.api-key}")
    private String apiKey;

    @Value("${gpt.model}")
    private String model;

    public SelfRepairGuide generateSelfRepairGuide(String type, String severity, String location,
                                                  Float area, String defectDescription, boolean selfRepairPossible) {
        String userPrompt = """
                하자 종류: %s
                심각도: %s
                위치: %s
                면적: %s㎡
                탐지 설명: %s
                자가 수리 가능 여부(확정): %s
                """.formatted(
                type,
                severity,
                location,
                area != null ? area : "미상",
                defectDescription != null && !defectDescription.isBlank() ? defectDescription : "없음",
                selfRepairPossible ? "가능" : "불가능");

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", userPrompt)),
                "response_format", Map.of(
                        "type", "json_schema",
                        "json_schema", Map.of(
                                "name", "self_repair_guide",
                                "strict", true,
                                "schema", responseSchema())));

        try {
            ChatCompletionResponse response = restTemplate.postForObject(
                    CHAT_COMPLETIONS_URL, authEntity(body), ChatCompletionResponse.class);

            String content = response.getChoices().get(0).getMessage().getContent();
            return objectMapper.readValue(content, SelfRepairGuide.class);
        } catch (Exception e) {
            log.error("GPT self-repair guide error: {}", e.getMessage());
            throw new CustomException(ErrorCode.DEFECT_002);
        }
    }

    private Map<String, Object> responseSchema() {
        Map<String, Object> item = Map.of(
                "type", "object",
                "properties", Map.of(
                        "name", Map.of("type", "string"),
                        "search_query", Map.of("type", "string"),
                        "estimated_price", Map.of("type", "integer")),
                "required", List.of("name", "search_query", "estimated_price"),
                "additionalProperties", false);

        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "description", Map.of("type", "string"),
                        "video_search_query", Map.of("type", "string"),
                        "items", Map.of("type", "array", "items", item)),
                "required", List.of("description", "video_search_query", "items"),
                "additionalProperties", false);
    }

    private HttpEntity<Map<String, Object>> authEntity(Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        return new HttpEntity<>(body, headers);
    }

    @Getter
    public static class SelfRepairGuide {

        private String description;

        @JsonProperty("video_search_query")
        private String videoSearchQuery;

        private List<GuideItem> items;
    }

    @Getter
    public static class GuideItem {

        private String name;

        @JsonProperty("search_query")
        private String searchQuery;

        @JsonProperty("estimated_price")
        private Integer estimatedPrice;
    }

    @Getter
    public static class ChatCompletionResponse {
        private List<Choice> choices;
    }

    @Getter
    public static class Choice {
        private Message message;
    }

    @Getter
    public static class Message {
        private String content;
    }
}
