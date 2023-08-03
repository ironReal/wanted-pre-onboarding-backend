package kr.co.wanted.wantedpreonboardingbackend.service;

import kr.co.wanted.wantedpreonboardingbackend.dto.BoardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardService {
    Long register(BoardDTO boardDTO);

    BoardDTO read(Long id);

    Page<BoardDTO> list(Pageable pageable);

    BoardDTO update(Long id, BoardDTO boardDTO);

    Long delete(Long id);
}
