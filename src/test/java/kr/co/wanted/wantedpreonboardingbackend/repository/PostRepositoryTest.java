package kr.co.wanted.wantedpreonboardingbackend.repository;

import kr.co.wanted.wantedpreonboardingbackend.domain.Member;
import kr.co.wanted.wantedpreonboardingbackend.domain.Post;
import kr.co.wanted.wantedpreonboardingbackend.dto.MemberJoinDTO;
import kr.co.wanted.wantedpreonboardingbackend.dto.PostDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    private PostDTO boardDTO;
    private Member member;

    @BeforeEach
    void setUp() {
        boardDTO = PostDTO.builder()
                .id(1L)
                .title("title")
                .content("content")
                .build();

        member = Member.from(MemberJoinDTO.builder()
                .email("test@google.com")
                .password("12345678")
                .build());

        memberRepository.save(member);
    }

    @Test
    @DisplayName("게시물 등록")
    void testSaveBoard() {
        final Post post = Post.from(boardDTO, member);
        final Post result = postRepository.save(post);

        assertThat(post.getTitle()).isEqualTo(result.getTitle());
        assertThat(post.getContent()).isEqualTo(result.getContent());
        assertThat(result.getId()).isNotNull();
    }
}

