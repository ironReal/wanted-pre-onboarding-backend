package kr.co.wanted.wantedpreonboardingbackend.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class JWTUtilTest {

    @Autowired
    private JWTUtil jwtUtil;

    private Map<String, Object> claimMap;

    @BeforeEach
    void before() {
        claimMap = Map.of("email", "test@google.com");
    }

    @Test
    @DisplayName("토큰 생성 확인")
    void testGenerate() {
        final String jwtStr = jwtUtil.generatedToken(claimMap, 1);
        log.info("JWT = {}", jwtStr);
        Assertions.assertThat(jwtStr).isNotNull();
    }
}