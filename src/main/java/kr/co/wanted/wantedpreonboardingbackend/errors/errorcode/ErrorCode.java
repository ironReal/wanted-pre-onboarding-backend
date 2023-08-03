package kr.co.wanted.wantedpreonboardingbackend.errors.errorcode;

import org.springframework.http.HttpStatus;

/**
 * 클라이언트에게 보내줄 에러 코드를 설정한 인터페이스.
 */
public interface ErrorCode {

    String name();

    HttpStatus getHttpStatus();
    String getMessage();
}
