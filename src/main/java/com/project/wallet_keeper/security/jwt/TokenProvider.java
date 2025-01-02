package com.project.wallet_keeper.security.jwt;

import com.project.wallet_keeper.security.util.AesUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

import static com.project.wallet_keeper.domain.Role.*;
import static java.util.concurrent.TimeUnit.*;

@Component
@Slf4j
public class TokenProvider implements InitializingBean {

    public static final String AUTHORITY = "authority";
    public static final String REFRESH_TOKEN_PREFIX = "refreshToken:";

    private SecretKey key;
    private SecretKey claimKey;

    private final String secret;
    private final long accessTokenExpireTime;
    private final long refreshTokenExpireTime;
    private final RedisTemplate<String, String> redisTemplate;

    public TokenProvider(
            @Value("${jwt.claim-key}") String claimKey,
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expire-time}") long accessTokenExpireTime,
            @Value("${jwt.refresh-token-expire-time}") long refreshTokenExpireTime,
            RedisTemplate<String, String> redisTemplate
    ) throws Exception {
        this.claimKey = AesUtil.generateKeyForString(claimKey);
        this.secret = secret;
        this.accessTokenExpireTime = accessTokenExpireTime * 1000;
        this.refreshTokenExpireTime = refreshTokenExpireTime * 1000;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] decode = Decoders.BASE64.decode(secret); // 비밀키 BASE64 디코딩 후
        this.key = Keys.hmacShaKeyFor(decode); // HMAC SHA 키 생성
    }

    public String generateAccessToken(String subject, String authority) throws Exception {
        String encryptSubject = AesUtil.encrypt(subject, claimKey);
        String encryptAuthority = AesUtil.encrypt(authority, claimKey);

        return createToken(encryptSubject, encryptAuthority, accessTokenExpireTime);
    }

    public String generateRefreshToken(String subject) throws Exception {
        String encryptSubject = AesUtil.encrypt(subject, claimKey);

        String refreshToken = createToken(encryptSubject, null, refreshTokenExpireTime);
        redisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + subject, refreshToken, refreshTokenExpireTime, MINUTES);
        return refreshToken;
    }

    private String createToken(String subject, String authority, long expireTime) {
        Date validity = calculateValidity(expireTime);

        JwtBuilder jwtBuilder = Jwts.builder()
                .subject(subject)
                .claim(AUTHORITY, authority)
                .issuedAt(new Date())
                .expiration(validity)
                .signWith(key);

        if (authority != null) {
            jwtBuilder.claim(AUTHORITY, authority);
        }

        return jwtBuilder.compact();
    }

    private Date calculateValidity(long expireTime) {
        long now = new Date().getTime();
        return new Date(now + expireTime);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            log.info("토큰 검증 성공!");
            return true;
        } catch (SignatureException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰입니다.");
            throw new ExpiredJwtException(null, null, "만료된 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("잘못된 토큰입니다.");
        }
        return false;
    }

    public Authentication getAuthentication(String token) throws Exception {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        if (claims == null) {
            throw new IllegalArgumentException("Claim이 비어있습니다.");
        }

        String subject = AesUtil.decrypt(claims.getSubject(), claimKey);
        String encryptAuthority = Optional.ofNullable(claims.get(AUTHORITY))
                .map(Object::toString)
                .orElse(AesUtil.encrypt(ROLE_USER.toString(), claimKey));
        String authority = AesUtil.decrypt(encryptAuthority, claimKey);

        return createAuthentication(subject, authority, token);
    }

    private Authentication createAuthentication(String subject, String authority, String token) {
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(authority));
        User principal = new User(subject, "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public String extractEmailFromToken(String token) throws Exception {
        String subject = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        return AesUtil.decrypt(subject, claimKey);
    }

    public String extractAuthorityFromToken(String accessToken) throws Exception {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();
        String encryptAuthority = (String) claims.get(AUTHORITY);
        return AesUtil.decrypt(encryptAuthority, claimKey);
    }

}
