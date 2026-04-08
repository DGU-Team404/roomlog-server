package com.roomlog.auth.service;

import com.roomlog.auth.domain.RefreshToken;
import com.roomlog.auth.dto.LoginRequest;
import com.roomlog.auth.dto.LoginResponse;
import com.roomlog.auth.dto.ReissueRequest;
import com.roomlog.auth.dto.ReissueResponse;
import com.roomlog.auth.dto.SignupRequest;
import com.roomlog.auth.dto.SignupResponse;
import com.roomlog.auth.repository.RefreshTokenRepository;
import com.roomlog.global.security.AuthToken;
import com.roomlog.global.exception.CustomException;
import com.roomlog.global.exception.ErrorCode;
import com.roomlog.user.domain.User;
import com.roomlog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthToken authToken;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.AUTH_002);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.AUTH_002);
        }

        return SignupResponse.from(user);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_001));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.AUTH_001);
        }

        String accessToken = authToken.generateToken(user.getId());
        String rawRefreshToken = authToken.generateRefreshToken(user.getId());

        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(authToken.getRefreshExpiration() / 1000);

        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .token(rawRefreshToken)
                .expiresAt(expiresAt)
                .build());

        return LoginResponse.of(user, accessToken, rawRefreshToken);
    }

    @Transactional
    public ReissueResponse reissue(ReissueRequest request) {
        authToken.getUserIdFromRefreshToken(request.getRefreshToken());

        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenAndIsRevokedFalse(request.getRefreshToken())
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_006));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshToken.revoke();
            throw new CustomException(ErrorCode.AUTH_006);
        }

        String newAccessToken = authToken.generateToken(refreshToken.getUser().getId());
        return ReissueResponse.of(newAccessToken);
    }
}
