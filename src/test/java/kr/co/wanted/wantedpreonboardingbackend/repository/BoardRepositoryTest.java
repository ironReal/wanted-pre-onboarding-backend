package kr.co.wanted.wantedpreonboardingbackend.repository;

import kr.co.wanted.wantedpreonboardingbackend.dto.BoardDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
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
    void testSaveBoard() {

        boardRepository.save()
    }
}