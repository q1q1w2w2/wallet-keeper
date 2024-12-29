package com.project.wallet_keeper.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Value("${email.googleId}")
    private String account;

    @Value("${email.appPassword}")
    private String password;

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl(); // JavaMailSender의 구현체 생성
        mailSender.setHost("smtp.gmail.com"); // SMTP 서버 호스트 설정
        mailSender.setPort(587); // 587: SMTP 포트
        mailSender.setUsername(account);
        mailSender.setPassword(password);

        Properties javaMailProperties = new Properties(); // JavaMail 속성 설정을 위한 Properties, (String,String) 으로 저장하는 컬렉션 클래스)
        javaMailProperties.put("mail.transport.protocol", "smtp");// SMTP 프로토콜 사용
        javaMailProperties.put("mail.smtp.auth", "true");// SMTP 서버 인증 필요
        javaMailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");// SSL 소켓 팩토리 클래스 사용
        javaMailProperties.put("mail.smtp.starttls.enable", "true");// STARTTLS(TLS를 시작하는 명령)를 사용하여 암호화된 통신을 활성화
        javaMailProperties.put("mail.debug", "true");// 디버깅 정보 출력
        javaMailProperties.put("mail.smtp.ssl.trust", "smtp.naver.com");// smtp 서버의 ssl 인증서를 신뢰
        javaMailProperties.put("mail.smtp.ssl.protocols", "TLSv1.2");// 사용할 ssl 프로토콜 버젼

        mailSender.setJavaMailProperties(javaMailProperties);

        return mailSender;
    }
}
