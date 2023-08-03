package kr.co.wanted.wantedpreonboardingbackend.security.exception;

import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * RefreshTokenFilter 동작 과정 중에서 여러 종루의 예외 사항이 발생을 처리하는 클래스
 */
public class RefreshTokenException extends RuntimeException{

    private final ErrorCase errorCase;

    public enum ErrorCase {
        NO_ACCESS, BAD_ACCESS, NO_REFRESH, OLD_REFRESH, BAD_REFRESH;
    }

    public RefreshTokenException(ErrorCase errorCase) {
        super(errorCase.name());
        this.errorCase = errorCase;
    }

    public void sendResponseError(HttpServletResponse response) {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();

        final String responseStr = gson.toJson(Map.of("message", errorCase.name(), "time", new Date()));

        try {
            response.getWriter().println(responseStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
