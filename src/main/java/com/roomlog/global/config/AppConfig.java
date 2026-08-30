package com.roomlog.global.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 외부 API(GPT·AI 서버·카카오·유튜브) 공용 RestTemplate.
     * 기본값은 타임아웃이 무제한이라 상대가 응답하지 않으면 요청 스레드가 영영 물린다.
     * AI 서버 호출은 접수 응답만 받고 결과는 콜백으로 오므로 30초면 모두 충분하다.
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
    }
}
