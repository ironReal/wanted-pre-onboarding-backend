package kr.co.wanted.wantedpreonboardingbackend.errors.exception;

import kr.co.wanted.wantedpreonboardingbackend.errors.errorcode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EmailNotFoundException extends RuntimeException {

    private final ErrorCode errorCode;
}