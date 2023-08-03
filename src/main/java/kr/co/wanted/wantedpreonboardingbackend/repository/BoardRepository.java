package kr.co.wanted.wantedpreonboardingbackend.repository;

import kr.co.wanted.wantedpreonboardingbackend.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
