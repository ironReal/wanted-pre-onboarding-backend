package kr.co.wanted.wantedpreonboardingbackend.service;

import kr.co.wanted.wantedpreonboardingbackend.domain.Member;
import kr.co.wanted.wantedpreonboardingbackend.dto.MemberJoinDTO;
import kr.co.wanted.wantedpreonboardingbackend.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberServiceImpl memberService;


    @Test
    @DisplayName("정상적인 회원가입")
    void testMemberJoin() {
        final String email = "test@google.com";
        final MemberJoinDTO memberJoinDTO = MemberJoinDTO.builder()
                .email(email)
                .password("12345678")
                .build();
        final Member member = Member.from(memberJoinDTO);

        when(memberRepository.save(any(Member.class))).thenReturn(member);

        final String findEmail = memberService.join(memberJoinDTO);

        assertThat(findEmail).isEqualTo(email);
    }
}