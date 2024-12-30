package com.project.wallet_keeper.security.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class AesUtil {

    public static final String ALGORITHM = "AES";

    // 비밀키 생성
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(128); // 128 비트로 설정
        return keyGenerator.generateKey();
    }

    public static SecretKey generateKeyForString(String keyString) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest(keyString.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(key, 0, 16, ALGORITHM);
    }

    // 문자열 암호화
    public static String encrypt(String data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM); //  AES 사용하는 Chiper 객체 생성
        cipher.init(Cipher.ENCRYPT_MODE, key); // 암호화 모드로 초기화
        byte[] encrypted = cipher.doFinal(data.getBytes()); // 암호화
        return Base64.getEncoder().encodeToString(encrypted); // 암호화된 바이트 배열을 BASE64로 인코딩하여 문자열로 변환
    }

    // 문자열 복호화
    public static String decrypt(String data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(data));
        return new String(decrypted);
    }
}
