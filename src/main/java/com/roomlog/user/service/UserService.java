package com.roomlog.user.service;

import com.roomlog.global.exception.CustomException;
import com.roomlog.global.exception.ErrorCode;
import com.roomlog.user.domain.User;
import com.roomlog.user.dto.DeleteUserResponse;
import com.roomlog.user.dto.GetMyProfileResponse;
import com.roomlog.user.dto.UpdateUserRequest;
import com.roomlog.user.dto.UpdateUserResponse;
import com.roomlog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public GetMyProfileResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMON_401));
        return GetMyProfileResponse.from(user);
    }

    @Transactional
    public UpdateUserResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMON_401));
        user.updateNickname(request.getNickname());
        return UpdateUserResponse.from(user);
    }

    @Transactional
    public DeleteUserResponse deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMON_401));
        user.softDelete();
        return DeleteUserResponse.from(user);
    }
}
