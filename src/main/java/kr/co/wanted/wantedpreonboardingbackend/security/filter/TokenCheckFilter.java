package kr.co.wanted.wantedpreonboardingbackend.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import kr.co.wanted.wantedpreonboardingbackend.security.APIUserDetailsService;
import kr.co.wanted.wantedpreonboardingbackend.security.exception.AccessTokenException;
import kr.co.wanted.wantedpreonboardingbackend.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 현자 사용자가 로그인한 사용자인지 체크하는 JWT 토큰을 검사하는 역할
 */
@Slf4j
@RequiredArgsConstructor
public class TokenCheckFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final APIUserDetailsService apiUserDetailsService;

    /**
     * 하나의 요청에 대해서 한번씩 동작하는 필터, Access Token에 문제가 생길 경우 자동으로 브라우저에 에러 메시지를 상태 코드와 함께 전송한다.
     * @param request HttpServletRequest
     * @param response HttpSServletResponse
     * @param filterChain FilterChain
     * @throws ServletException ServletException
     * @throws IOException IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String path = request.getRequestURI();

        if (!path.startsWith("/api/") || path.equals("/api/member/join")) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("TOKEN CHECK FILTER");

        try {

            final Map<String, Object> payload = validateAccessToken(request);

            final String email = (String) payload.get("email");

            log.info(email);

            final UserDetails userDetails = apiUserDetailsService.loadUserByUsername(email);
            final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);

        } catch (AccessTokenException accessTokenException) {
            accessTokenException.sendResponseError(response);
        }
    }

    /**
     * JWTUtil 을 validationToken()을 실행해서 문제가 생기면 발생하는 JwtException 을 이용해서 예외 내용을 출력하고 AccessTokenException 을 던지도록 한다.
     * @param request
     * @return 검증된 문자열
     */
    private Map<String, Object> validateAccessToken(HttpServletRequest request) {

        String headerStr = request.getHeader("Authorization");

        if (headerStr == null || headerStr.length() < 8) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.UNACCEPT);
        }

        // Bearer 생략
        String tokenType = headerStr.substring(0, 6);
        String tokenStr = headerStr.substring(7);

        if (!tokenType.equalsIgnoreCase("Bearer")) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADTYPE);
        }

        try {

            return jwtUtil.validationToken(tokenStr);

        } catch (MalformedJwtException malformedJwtException) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.MALFORM);
        } catch (SignatureException signatureException) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADSIGN);
        } catch (ExpiredJwtException expiredJwtException) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.EXPIRED);
        }
    }
}
