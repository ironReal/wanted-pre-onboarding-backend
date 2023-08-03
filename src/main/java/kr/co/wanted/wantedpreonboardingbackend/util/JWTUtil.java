package kr.co.wanted.wantedpreonboardingbackend.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtil {

    @Value("${kr.co.wantedpreonboardingbackend.jwt.secret}")
    private String key;

    /**
     * JWT 문자열 생성
     * @param valueMap eamil, password 정보가 담긴 Map
     * @param days 유효기간
     * @return JWT 문자열
     */
    public String generatedToken(Map<String, Object> valueMap, int days) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        Map<String, Object> payload = new HashMap<>(valueMap);

        int time = (60 * 24) * days;

        return Jwts.builder()
                .setHeader(headers)
                .setClaims(payload)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(time).toInstant()))
                .signWith(SignatureAlgorithm.HS256, key.getBytes())
                .compact();
    }

    /**
     * JWT 문자열 검증
     * JWT 문자열 자체의 구성이 잘못되었거나, JWT 문자열의 유효기간이 지났거나, 서명에 문제가 있는 등을 검사한다
     * @param token JWT 문자열
     * @return
     * @throws JwtException
     */
    public Map<String, Object> validationToken(String token) throws JwtException {
        return Jwts.parser()
                .setSigningKey(key.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }
}
