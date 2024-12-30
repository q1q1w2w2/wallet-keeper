package com.project.wallet_keeper.service;

import com.project.wallet_keeper.domain.User;
import com.project.wallet_keeper.dto.user.SignupDto;
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
    public User signUp(SignupDto signupDto) {

        if (userRepository.findByEmail(signupDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistException("이미 가입되어 있는 이메일입니다.");
        }
        if (userRepository.findByNickname(signupDto.getNickname()).isPresent()) {
            throw new UserAlreadyExistException("이미 존재하는 닉네임입니다.");
        }

        User user = User.builder()
                .email(signupDto.getEmail())
                .password(passwordEncoder.encode(signupDto.getPassword()))
                .nickname(signupDto.getNickname())
                .birth(signupDto.getBirth())
                .build();
        return userRepository.save(user);
    }
}
