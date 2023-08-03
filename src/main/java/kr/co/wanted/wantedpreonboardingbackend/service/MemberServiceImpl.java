package kr.co.wanted.wantedpreonboardingbackend.service;

import kr.co.wanted.wantedpreonboardingbackend.errors.errorcode.CommonErrorCode;
import kr.co.wanted.wantedpreonboardingbackend.errors.errorcode.ErrorCode;
import kr.co.wanted.wantedpreonboardingbackend.errors.exception.EmailExistException;
import kr.co.wanted.wantedpreonboardingbackend.domain.Member;
import kr.co.wanted.wantedpreonboardingbackend.dto.MemberJoinDTO;
import kr.co.wanted.wantedpreonboardingbackend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String join(MemberJoinDTO memberJoinDTO) {
        final String email = memberJoinDTO.getEmail();
        final boolean exist = memberRepository.existsByEmail(email);

        if (exist) {
            throw new EmailExistException(CommonErrorCode.INVALID_PARAMETER);
        }

        Member member = modelMapper.map(memberJoinDTO, Member.class);
        member.changePassword(passwordEncoder.encode(memberJoinDTO.getPassword()));
        log.info("JOIN MEMBER = {}", member);

        memberRepository.save(member);

        return member.getEmail();
    }
}
