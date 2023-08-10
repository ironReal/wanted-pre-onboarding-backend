package kr.co.wanted.wantedpreonboardingbackend.repository;

import kr.co.wanted.wantedpreonboardingbackend.domain.Post;
import kr.co.wanted.wantedpreonboardingbackend.dto.PostDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    private PostDTO boardDTO;

    @BeforeEach
    void setUp() {
        boardDTO = PostDTO.builder()
                .id(1L)
                .title("title")
                .content("content")
                .build();
    }

    @Test
    @DisplayName("게시물 등록")
    void testSaveBoard() {
        final Post post = Post.from(boardDTO);
        final Post result = postRepository.save(post);

        assertThat(post.getTitle()).isEqualTo(result.getTitle());
        assertThat(post.getContent()).isEqualTo(result.getContent());
        assertThat(result.getId()).isNotNull();
    }

    @Test
    @DisplayName("아이디로 게시물 조회")
    void test() {
        final Post createdBoard = Post.from(boardDTO);
        postRepository.save(createdBoard);

        final Optional<Post> result = postRepository.findById(createdBoard.getId());
        final Post board = result.orElseThrow(() -> new IllegalArgumentException("not found board id"));

        assertThat(board).isNotNull();
        assertThat(board.getId()).isEqualTo(1L);
    }
}

