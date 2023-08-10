package kr.co.wanted.wantedpreonboardingbackend.repository;

import kr.co.wanted.wantedpreonboardingbackend.domain.Member;
import kr.co.wanted.wantedpreonboardingbackend.dto.MemberJoinDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 저장")
    void testSaveMember() {
        final MemberJoinDTO joinDTO = MemberJoinDTO.builder()
                .email("test@google.com")
                .password("12345678")
                .build();
        final Member member = Member.from(joinDTO);
        final Member savedMember = memberRepository.save(member);

        assertThat(member).isEqualTo(savedMember);
        assertThat(member.getEmail()).isEqualTo(savedMember.getEmail());
        assertThat(savedMember.getId()).isNotNull();
    }

    @Test
    @DisplayName("이메일로 회원 조회")
    void testFindMember() {
        final MemberJoinDTO joinDTO = MemberJoinDTO.builder()
                .email("test@google.com")
                .password("12345678")
                .build();
        final Member member = Member.from(joinDTO);
        final Member savedMember = memberRepository.save(member);
        final Optional<Member> result = memberRepository.findByEmail(savedMember.getEmail());
        final Member findMember = result.orElseThrow(() -> new IllegalArgumentException("not found member email = " + savedMember.getEmail()));

        assertThat(findMember.getEmail()).isEqualTo("test@google.com");
        assertThat(findMember.getId()).isEqualTo(1L);
    }
}