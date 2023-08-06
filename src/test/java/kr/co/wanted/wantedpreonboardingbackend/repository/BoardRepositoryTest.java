package kr.co.wanted.wantedpreonboardingbackend.repository;

import kr.co.wanted.wantedpreonboardingbackend.domain.Board;
import kr.co.wanted.wantedpreonboardingbackend.dto.BoardDTO;
import kr.co.wanted.wantedpreonboardingbackend.errors.exception.BoardIdNotFound;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    private BoardDTO boardDTO;

    @BeforeEach
    void setUp() {
        boardDTO = BoardDTO.builder()
                .id(1L)
                .title("title")
                .content("content")
                .build();
    }

    @Test
    @DisplayName("게시물 등록")
    void testSaveBoard() {
        final Board board = Board.from(boardDTO);
        final Board result = boardRepository.save(board);

        assertThat(board.getTitle()).isEqualTo(result.getTitle());
        assertThat(board.getContent()).isEqualTo(result.getContent());
        assertThat(result.getId()).isNotNull();
    }

    @Test
    @DisplayName("아이디로 게시물 조회")
    void test() {
        final Board createdBoard = Board.from(boardDTO);
        boardRepository.save(createdBoard);

        final Optional<Board> result = boardRepository.findById(boardDTO.getId());
        final Board board = result.orElseThrow(() -> new IllegalArgumentException("not found board id"));

        assertThat(board).isNotNull();
        assertThat(board.getId()).isEqualTo(1L);
    }
}
