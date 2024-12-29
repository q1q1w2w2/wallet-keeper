package com.project.wallet_keeper.service;

import com.project.wallet_keeper.domain.User;
import com.project.wallet_keeper.dto.SignupDto;
import com.project.wallet_keeper.exception.UserAlreadyExistException;
import com.project.wallet_keeper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public User signUp(SignupDto dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserAlreadyExistException("이미 가입되어 있는 이메일입니다.");
        }
        if (userRepository.findByNickname(dto.getNickname()).isPresent()) {
            throw new UserAlreadyExistException("이미 존재하는 닉네임입니다.");
        }

        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .birth(dto.getBirth())
                .build();
        return userRepository.save(user);
    }
}
