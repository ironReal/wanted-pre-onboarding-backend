package kr.co.wanted.wantedpreonboardingbackend.repository;

import kr.co.wanted.wantedpreonboardingbackend.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String username);
}
