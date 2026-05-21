package com.roomlog.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomlog.global.exception.ErrorCode;
import com.roomlog.global.response.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-Api-Key";
    private static final List<String> AI_CALLBACK_PATHS = List.of(
            "/analyses/*/result",
            "/scans/*/result"
    );

    private final String apiKey;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        AntPathMatcher matcher = new AntPathMatcher();
        return AI_CALLBACK_PATHS.stream().noneMatch(p -> matcher.match(p, request.getServletPath()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String key = request.getHeader(API_KEY_HEADER);

        if (!apiKey.equals(key)) {
            response.setStatus(ErrorCode.COMMON_401.getHttpStatus().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), ApiResponse.failure(ErrorCode.COMMON_401));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
