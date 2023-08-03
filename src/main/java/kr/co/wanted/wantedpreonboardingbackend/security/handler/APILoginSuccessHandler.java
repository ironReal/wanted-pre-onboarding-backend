package kr.co.wanted.wantedpreonboardingbackend.security.handler;

import com.google.gson.Gson;
import kr.co.wanted.wantedpreonboardingbackend.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 *
 * 인증 성공 후 처리 작업을 담당하여 AuthenticationSuccessHandler 이용해서 후처리를 진행한다. APILoginFilter 가 동작하고 인증 처리가 된 후에는 동작한다.
 */
@Slf4j
@RequiredArgsConstructor
public class APILoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    /**
     * 인증된 사용자에게 Access Token / Refresh Token 발행
     * @param request the request which caused the successful authentication
     * @param response the response
     * @param authentication the <tt>Authentication</tt> object which was created during
     * the authentication process.
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("APILoginSuccessHandler");

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        log.info(authentication.getName());

        Map<String, Object> claim = Map.of("email", authentication.getName());

        // Access token 유효기간 1일
        final String accessToken = jwtUtil.generatedToken(claim, 1);

        // Refresh token 유효기간 10일
        final String refreshToken = jwtUtil.generatedToken(claim, 10);

        Gson gson = new Gson();

        Map<String, String> keyMap = Map.of("accessToken", accessToken, "refreshToken", refreshToken);

        final String jsonStr = gson.toJson(keyMap);
        log.info("json = {}", jsonStr);

        response.getWriter().println(jsonStr);
    }
}
