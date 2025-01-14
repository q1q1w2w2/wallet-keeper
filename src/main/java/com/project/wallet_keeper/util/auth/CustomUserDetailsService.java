package com.project.wallet_keeper.util.auth;

import com.project.wallet_keeper.entity.User;
import com.project.wallet_keeper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service("userDetailsService")
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        // email이 틀려도 AbstractUserDetailsAuthenticationProvider에서 BadCredentialException으로 변환
        return userRepository.findByEmail(email)
                .map(user -> createUser(email, user))
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private org.springframework.security.core.userdetails.User createUser(String email, User user) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().toString());
        return new org.springframework.security.core.userdetails.User(
                email, user.getPassword(), Collections.singleton(authority)
        );
    }
}
