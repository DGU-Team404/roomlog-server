package com.roomlog.chat.controller;

import com.roomlog.chat.dto.CreateChatSessionResponse;
import com.roomlog.chat.dto.GetChatMessagesResponse;
import com.roomlog.chat.dto.SendChatMessageRequest;
import com.roomlog.chat.dto.SendChatMessageResponse;
import com.roomlog.chat.service.ChatService;
import com.roomlog.global.response.ApiResponse;
import com.roomlog.global.security.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "C01. 대화 시작", description = """
            챗봇 대화 세션을 생성합니다. 챗봇 화면에 진입할 때 호출합니다.

            greeting(첫 인사말)과 suggested_questions(추천 질문 목록)가 함께 내려갑니다.
            추천 질문을 버튼으로 노출하고, 사용자가 누르면 C02에 question은 message로, guide는 guide로 실어 보내주세요.""",
            tags = "8. Chat")
    @PostMapping("/sessions")
    public ApiResponse<CreateChatSessionResponse> createSession(@AuthenticationPrincipal LoginUser loginUser) {
        CreateChatSessionResponse response = chatService.createSession(loginUser.userId());
        return ApiResponse.success(201, "대화가 시작되었습니다.", response);
    }

    @Operation(summary = "C02. 메시지 전송", description = """
            질문을 보내고 답변을 받습니다. 앱 사용법만 안내하며 그 밖의 질문에는 안내가 어렵다고 답합니다.

            - message: 사용자 질문 (최대 300자)
            - guide: 추천 질문 버튼으로 보낸 경우 해당 버튼의 guide 값. 직접 입력한 질문이면 생략합니다.

            source로 답변 생성 방식을 알 수 있습니다.
            GUIDE(고정 안내문) · CACHE(이전 답변 재사용) · GPT(신규 생성) · FALLBACK(안내 범위 밖으로 보이는 질문).
            FALLBACK인 경우에만 suggested_questions가 함께 내려갑니다.""",
            tags = "8. Chat")
    @PostMapping("/sessions/{sessionId}/messages")
    public ApiResponse<SendChatMessageResponse> sendMessage(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "대화 세션 ID", example = "1") @PathVariable Long sessionId,
            @Valid @RequestBody SendChatMessageRequest request) {

        SendChatMessageResponse response = chatService.sendMessage(loginUser.userId(), sessionId, request);
        return ApiResponse.success(200, "답변 생성에 성공했습니다.", response);
    }

    @Operation(summary = "C03. 대화 내역 조회", description = "세션의 전체 대화 내역을 오래된 순으로 조회합니다. 앱을 다시 열었을 때 이전 대화를 복원하는 데 사용합니다.",
            tags = "8. Chat")
    @GetMapping("/sessions/{sessionId}/messages")
    public ApiResponse<GetChatMessagesResponse> getMessages(
            @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "대화 세션 ID", example = "1") @PathVariable Long sessionId) {

        GetChatMessagesResponse response = chatService.getMessages(loginUser.userId(), sessionId);
        return ApiResponse.success(200, "대화 내역 조회에 성공했습니다.", response);
    }
}
