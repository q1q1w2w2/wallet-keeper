package com.project.wallet_keeper.service;

import com.project.wallet_keeper.entity.Reason;
import com.project.wallet_keeper.entity.User;
import com.project.wallet_keeper.dto.user.ResetPasswordDto;
import com.project.wallet_keeper.dto.user.SignupDto;
import com.project.wallet_keeper.dto.user.UpdatePasswordDto;
import com.project.wallet_keeper.dto.user.UserProfileUpdateDto;
import com.project.wallet_keeper.exception.user.UserAlreadyExistException;
import com.project.wallet_keeper.exception.user.UserNotFoundException;
import com.project.wallet_keeper.repository.ReasonRepository;
import com.project.wallet_keeper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.project.wallet_keeper.entity.Role.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ReasonRepository reasonRepository;

    @Transactional
    public User signUp(SignupDto signupDto) {
        if (userRepository.findByEmail(signupDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistException("이미 가입되어 있는 이메일입니다.");
        }

        User user = User.builder()
                .email(signupDto.getEmail())
                .password(passwordEncoder.encode(signupDto.getPassword()))
                .nickname(signupDto.getNickname())
                .birth(signupDto.getBirth())
                .role(ROLE_USER)
                .build();

        return userRepository.save(user);
    }

    @Transactional
    @CacheEvict(value = "user", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public User updateUser(User user, UserProfileUpdateDto updateDto) {
         return user.update(updateDto.getNickname(), updateDto.getBirth());
    }

    @Transactional
    @CacheEvict(value = "user", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public void deleteUser(User user, String reason) {
        user.deleteUser();
        reasonRepository.save(new Reason(reason));
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    @Cacheable(value = "user", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public User getCurrentUser() {
        String email = getEmailFromAuthentication();
        return getUserByEmail(email);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    public String getEmailFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails userDetails) {
                String username = userDetails.getUsername();
                log.info("현재 사용자: {}", username);
                return username;
            } else if (principal instanceof String) {
                return (String) principal;
            }
        }
        throw new UserNotFoundException("현재 로그인한 사용자를 찾을 수 없습니다.");
    }

    @Transactional
    @CacheEvict(value = "user", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public void updatePassword(User user, UpdatePasswordDto passwordDto) {
        String oldPassword = passwordDto.getOldPassword();
        String newPassword = passwordDto.getNewPassword();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public boolean resetPassword(ResetPasswordDto passwordDto) {
        String email = passwordDto.getEmail();
        String password = passwordDto.getPassword();

        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        user.updatePassword(passwordEncoder.encode(password));
        return true;
    }
}
