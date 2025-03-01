package com.project.wallet_keeper.service;

import com.project.wallet_keeper.exception.auth.VerificationCodeMismatchException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

import static java.util.concurrent.TimeUnit.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MailService {

    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String AUTH_CODE_PREFIX = "authCode:";

    public void sendMailForSignup(String email) throws MessagingException {
        String randomNumber = generateNumber();
        String sender = "xmrrhdwjdqls@gmail.com";
        String title = "[WalletKeeper] 회원 가입 인증 메일입니다.";
        String content =
                        "<html>" +
                        "<h1 style='color: #333;'>회원 가입 인증 메일</h1>" +
                        "<p>안녕하세요!</p>" +
                        "<p>회원 가입을 위해 아래의 인증 번호를 입력해 주세요:</p>" +
                        "<h2 style='color: #007BFF;'>" + randomNumber + "</h2>" +
                        "<p style='font-size: 12px; color: #777;'>이 인증 번호는 5분 동안 유효합니다.</p>" +
                        "</html>";

        redisTemplate.opsForValue().set(AUTH_CODE_PREFIX + email, randomNumber, 5, MINUTES);

        sendMail(sender, email, title, content);
    }

    private void sendMail(String sender, String receiver, String title, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
        helper.setFrom(sender);
        helper.setTo(receiver);
        helper.setSubject(title);
        helper.setText(content, true);
        mailSender.send(message);
    }

    private String generateNumber() {
        Random random = new Random();
        StringBuilder numberBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            numberBuilder.append(random.nextInt(10));
        }
        return numberBuilder.toString();
    }

    public boolean verifyCode(String email, String code) {
        String storeCode = redisTemplate.opsForValue().get(AUTH_CODE_PREFIX + email);
        if (storeCode == null) {
            throw new VerificationCodeMismatchException("인증 코드가 존재하지 않습니다.");
        }

        if (storeCode.equals(code)) {
            return true;
        } else {
            throw new VerificationCodeMismatchException();
        }
    }
}
