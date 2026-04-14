package com.roomlog.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
                        new Tag().name("1. 인증").description("회원가입, 로그인, 토큰 재발급 API"),
                        new Tag().name("2. 방").description("방 목록 조회, 상세 조회, 수정, 삭제 API"),
                        new Tag().name("3. Viewer").description("3D 뷰어 조회, 스캔 목록, 하자 상세 조회 API"),
                        new Tag().name("4. 스캔").description("3D 스캔 업로드, 상태 조회, 미리보기 API"),
                        new Tag().name("5. 분석").description("하자 분석 생성, 결과 조회 API"),
                        new Tag().name("6. 하자").description("하자 목록 조회, 상세 조회 API"),
                        new Tag().name("7. 견적").description("견적 요청, 목록 조회, 상세 조회 API"),
                        new Tag().name("8. 수리").description("수리 내역 조회, 수리 완료 등록 API"),
                        new Tag().name("9. 마이페이지").description("프로필 조회, 내 정보 수정, 회원 탈퇴 API")
                ));
    }
}
