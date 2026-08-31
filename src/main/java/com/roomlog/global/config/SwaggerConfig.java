package com.roomlog.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String securitySchemeName = "BearerAuth";

        return new OpenAPI()
                .servers(List.of(new Server().url("https://roomlog-server-production.up.railway.app")))
                .info(new Info()
                        .title("RoomLog API")
                        .description("RoomLog 서버 API 명세서")
                        .version("v1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .tags(List.of(
                        new Tag().name("1. House").description("집 생성/조회/수정/삭제, 대표 집 설정, 집 내 방 목록 조회 API"),
                        new Tag().name("2. Room").description("방 생성/상세조회/수정/삭제 API"),
                        new Tag().name("3. Scan").description("3D 스캔 업로드, 상태 조회, 미리보기 API"),
                        new Tag().name("4. Viewer").description("3D 뷰어, 비교 시점 선택, 분석 생성/조회, 수리비 요약 API"),
                        new Tag().name("5. Repair").description("업체 조회, 견적 요청/조회, 수리 내역 조회, 수리 완료 등록 API"),
                        new Tag().name("6. Auth").description("회원가입, 로그인, 토큰 재발급 API"),
                        new Tag().name("7. MyPage").description("프로필 조회, 내 정보 수정, 회원 탈퇴 API"),
                        new Tag().name("8. Chat").description("앱 사용법 안내 챗봇 대화 시작, 메시지 전송, 대화 내역 조회, 등록된 하자 목록 조회 API"),
                        new Tag().name("0. Internal").description("AI 서버 내부 전용 API")
                ));
    }
}
