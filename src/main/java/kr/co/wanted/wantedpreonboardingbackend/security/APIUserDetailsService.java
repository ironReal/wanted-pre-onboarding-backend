package kr.co.wanted.wantedpreonboardingbackend.security;

import kr.co.wanted.wantedpreonboardingbackend.domain.Member;
import kr.co.wanted.wantedpreonboardingbackend.dto.MemberLoginDTO;
import kr.co.wanted.wantedpreonboardingbackend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class APIUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        final Optional<Member> result = memberRepository.findByEmail(username);

        final Member member = result.orElseThrow(() -> new UsernameNotFoundException("Cannot find email"));

        final MemberLoginDTO dto = new MemberLoginDTO(
                member.getEmail(),
                member.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        log.info("member dto = {}", dto);

        return dto;
    }
}
