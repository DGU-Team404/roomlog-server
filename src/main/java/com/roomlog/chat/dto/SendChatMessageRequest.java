package com.roomlog.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendChatMessageRequest {

    @NotBlank
    @Size(max = 300)
    private String message;

    /** 추천 질문 버튼으로 보낸 경우의 안내 섹션 코드. 값이 있으면 GPT를 호출하지 않는다. */
    private String guide;

    /** "등록된 하자에서 선택하기"로 고른 하자 ID. 값이 있으면 하자 상담으로 답한다. */
    @JsonProperty("defect_id")
    private Long defectId;
}
