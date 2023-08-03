package kr.co.wanted.wantedpreonboardingbackend.security.exception;

import com.google.gson.Gson;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * 발생하는 예외의 종류를 미리 enum 으로 구분해 두고, 나중에 에러 메시지를 전송할 수 있는 구조
 */
public class AccessTokenException extends RuntimeException{

    TOKEN_ERROR token_error;

    public enum TOKEN_ERROR {
        UNACCEPT(401, "Token is null or too start"),
        BADTYPE(401, "Token type Bearer"),
        MALFORM(403, "Malformed Token"),
        BADSIGN(403, "BadSignature Token"),
        EXPIRED(403, "Expired Token");

        private final int status;
        private final String message;

        TOKEN_ERROR(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }

    public AccessTokenException(TOKEN_ERROR error) {
        super(error.name());
        this.token_error = error;
    }

    public void sendResponseError(HttpServletResponse response) {

        response.setStatus(token_error.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();

        String responseStr = gson.toJson(Map.of("message", token_error.getMessage(), "time", new Date()));

        try {
            response.getWriter().println(responseStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
