package kr.co.wanted.wantedpreonboardingbackend.security.filter;

import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import kr.co.wanted.wantedpreonboardingbackend.security.exception.RefreshTokenException;
import kr.co.wanted.wantedpreonboardingbackend.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * RefreshToken 검사해서 토큰이 없거나 잘못된 토큰 혹은 만료된 토큰인 경우 에러 메시지 전송
 */
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {

    private final String refreshPath;
    private final JWTUtil jwtUtil;

    /**
     * refreshToken 발행 시 accessToken 함께 발행한다
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String path = request.getRequestURI();

        if (!path.equals(refreshPath)) {
            log.info("skip refresh token filter");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("REFRESH TOKEN FILTER RUN");

        final Map<String, String> tokens = parseRequestJSON(request);

        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");

        log.info(accessToken);
        log.info(refreshToken);

        // accessToken 검증
        try {
            checkAccessToken(accessToken);
        } catch (RefreshTokenException refreshTokenException) {
            refreshTokenException.sendResponseError(response);
        }

        // refreshToken 검증
        Map<String, Object> refreshClaims = null;

        try {
            refreshClaims = checkRefreshToken(refreshToken);
        } catch (RefreshTokenException refreshTokenException) {
            refreshTokenException.sendResponseError(response);
        }

        // RefreshToken 유효 시간이 얼마 남지 않은 경우
        final Integer exp = (Integer) refreshClaims.get("exp");
        final Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() * 1000);
        final Date current = new Date(System.currentTimeMillis());

        // 만료 시간과 현재 시간의 간격 계산
        long gapTime = expTime.getTime() - current.getTime();

        String email = (String) refreshClaims.get("email");
        String accessTokenValue = jwtUtil.generatedToken(Map.of("email", email), 1);
        String refreshTokenValue = tokens.get("refreshToken");

        // RefreshToken이 3일도 안남았을 시
        if (gapTime < (1000 * 60 * 60 * 24 * 3)) {
            refreshTokenValue = jwtUtil.generatedToken(Map.of("email", email), 30);
        }

        sendToken(accessTokenValue, refreshTokenValue, response);
    }

    /**
     * 최종적으로 만들어진 토큰들을 전송하는 메서드
     * @param accessTokenValue
     * @param refreshTokenValue
     * @param response
     */
    private void sendToken(String accessTokenValue, String refreshTokenValue, HttpServletResponse response) {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();

        final String jsonStr = gson.toJson(Map.of("accessToken", accessTokenValue, "refreshToken", refreshTokenValue));

        try {
            response.getWriter().println(jsonStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> parseRequestJSON(HttpServletRequest request) {

        try (Reader reader = new InputStreamReader(request.getInputStream())) {

            Gson gson = new Gson();

            return gson.fromJson(reader, Map.class);

        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return Collections.emptyMap();
    }

    /**
     * accessToken 검증. 문제가 생기면 RefreshTokenException 을 전달한다.
     * @param accessToken accessToken
     * @throws RefreshTokenException
     */
    private void checkAccessToken(String accessToken) throws RefreshTokenException {
        try {
            jwtUtil.validationToken(accessToken);
        } catch (ExpiredJwtException expiredJwtException) {
            log.info("Access Token has Expired");
        } catch (Exception e) {
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_ACCESS);
        }
    }

    private Map<String, Object> checkRefreshToken(String refreshToken) throws RefreshTokenException {
        try {

            return jwtUtil.validationToken(refreshToken);

        } catch (ExpiredJwtException expiredJwtException) {
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.OLD_REFRESH);
        } catch (MalformedJwtException malformedJwtException) {
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
        } catch (Exception e) {
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.BAD_REFRESH);
        }
    }
}
