package kr.co.wanted.wantedpreonboardingbackend.controller;

import kr.co.wanted.wantedpreonboardingbackend.dto.MemberJoinDTO;
import kr.co.wanted.wantedpreonboardingbackend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@Transactional
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<String> memberJoin(@Valid @RequestBody MemberJoinDTO memberJoinDTO) {
        log.info("JOIN MEMBER CONTROLLER");
        log.info(String.valueOf(memberJoinDTO));

        final String email = memberService.join(memberJoinDTO);

        return new ResponseEntity<>(email, HttpStatus.CREATED);
    }
}
