package kr.co.wanted.wantedpreonboardingbackend.security.filter;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.Map;

/**
 *
 * 인증 단계를 처리하고 인증에 성공했을 때는 Access Token 과 Refresh Token 을 전송한다.
 */
@Slf4j
public class ApiLoginFilter extends AbstractAuthenticationProcessingFilter {

    public ApiLoginFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        if (request.getMethod().equalsIgnoreCase("GET")) {
            log.error("GET METHOD NOT SUPPORT");
            return null;
        }

        Map<String, String> jsonData = parseRequestJSON(request);
        log.info("API Login Filter = {}", jsonData);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        jsonData.get("email"),
                        jsonData.get("password")
                );

        return getAuthenticationManager().authenticate(authenticationToken);
    }

    /**
     * JSON 데이터를 분석해서 email, password 전달 값을 Map 으로 처리
     * @param request
     * @return email, password 를 담은 Map
     */
    private Map<String, String> parseRequestJSON(HttpServletRequest request) {

        try (Reader reader = new InputStreamReader(request.getInputStream())) {

            Gson gson = new Gson();

            return gson.fromJson(reader, Map.class);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Collections.emptyMap();
    }
}
