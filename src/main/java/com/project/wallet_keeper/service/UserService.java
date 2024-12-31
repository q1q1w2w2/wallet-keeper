package com.project.wallet_keeper.service;

import com.project.wallet_keeper.domain.User;
import com.project.wallet_keeper.dto.user.SignupDto;
import com.project.wallet_keeper.dto.user.UserProfileUpdateDto;
import com.project.wallet_keeper.exception.UserAlreadyExistException;
import com.project.wallet_keeper.exception.UserNotFoundException;
import com.project.wallet_keeper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.project.wallet_keeper.domain.Role.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    /**
     * 사용자 회원가입
     *
     * @param signupDto 사용자 가입에 필요한 정보
     * @return 생성된 사용자 객체
     * @throws UserAlreadyExistException 이미 존재하는 이메일 또는 닉네임일 경우 예외 발생
     */
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
                .role(ROLE_USER)
                .build();
        return userRepository.save(user);
    }

    /**
     * 현재 로그인한 사용자 정보 수정
     *
     * @param user 현재 로그인한 사용자
     * @param updateDto 변경할 nickname과 birth
     * @return 변경된 User 객체
     * @throws UserAlreadyExistException 변경할 닉네임이 이미 존재하는 경우 예외 발생
     */
    @Transactional
    public User updateUser(User user, UserProfileUpdateDto updateDto) {
        if (userRepository.findByNickname(updateDto.getNickname()).isPresent()) {
            throw new UserAlreadyExistException("이미 존재하는 닉네임입니다.");
        }

        return user.update(updateDto.getNickname(), updateDto.getBirth());
    }

    /**
     * 현재 로그인한 사용자 회원 탈퇴 및 연관 데이터 삭제
     *
     * @param user 현재 로그인한 사용자
     * @throws
     */
    @Transactional
    public void deleteUser(User user) {
        // todo 연관 데이터도 함께 삭제해줘야 함
        userRepository.delete(user);
    }

    /**
     * userId로 사용자 정보를 반환
     *
     * @param userId 유저 고유 번호
     * @return User 객체
     * @throws UserNotFoundException 해당 ID의 사용자가 존재하지 않을 경우 예외 발생
     */
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    /**
     * 현재 로그인한 사용자 정보를 반환
     *
     * @return 현재 사용자 User 객체
     * @throws UserNotFoundException 현재 로그인한 사용자가 없을 경우 예외 발생
     */
    public User getCurrentUser() {
        String email = getEmailFromAuthentication();
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    /**
     * 현재 로그인한 사용자의 이메일 반환
     *
     * @return 현재 사용자의 이메일
     * @throws UserNotFoundException 현재 로그인한 사용자가 없을 경우 예외 발생
     */
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
}
