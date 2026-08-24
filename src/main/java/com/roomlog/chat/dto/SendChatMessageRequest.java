package com.roomlog.chat.dto;

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
}
