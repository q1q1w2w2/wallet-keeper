package com.project.wallet_keeper.service;

import com.project.wallet_keeper.exception.auth.VerificationCodeMismatchException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MailServiceTest {

    @InjectMocks
    private MailService mailService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        mailService = new MailService(mailSender, redisTemplate);
    }

    @Test
    @DisplayName("회원가입 인증 메일 발송")
    void sendMailForSignup() throws MessagingException {
        // given
        String email = "test@gmail.com";
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // when
        mailService.sendMailForSignup(email);

        // then
        verify(valueOperations, times(1))
                .set(startsWith("authCode:"), anyString(), eq(5L), eq(java.util.concurrent.TimeUnit.MINUTES));
    }

    @Test
    @DisplayName("인증 코드 일치 확인 성공")
    void verifyCode_Success() {
        // given
        String email = "test@example.com";
        String code = "123456";
        when(valueOperations.get("authCode:" + email)).thenReturn(code);

        // when & then
        assertTrue(mailService.verifyCode(email, code));
    }

    @Test
    @DisplayName("인증 코드 불일치")
    void verifyCode_Fail_Mismatch() {
        // given
        String email = "test@example.com";
        when(valueOperations.get("authCode:" + email)).thenReturn("654321");

        // when & then
        assertThrows(VerificationCodeMismatchException.class, () ->
                mailService.verifyCode(email, "123456"));
    }

    @Test
    @DisplayName("인증 코드 없음")
    void verifyCode_Fail_NotExist() {
        // given
        String email = "test@example.com";
        when(valueOperations.get("authCode:" + email)).thenReturn(null);

        // when & then
        assertThrows(VerificationCodeMismatchException.class, () ->
                mailService.verifyCode(email, "123456"));
    }
}