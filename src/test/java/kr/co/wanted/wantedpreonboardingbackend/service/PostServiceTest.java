package kr.co.wanted.wantedpreonboardingbackend.service;

import kr.co.wanted.wantedpreonboardingbackend.domain.Post;
import kr.co.wanted.wantedpreonboardingbackend.domain.Member;
import kr.co.wanted.wantedpreonboardingbackend.dto.PostDTO;
import kr.co.wanted.wantedpreonboardingbackend.dto.MemberJoinDTO;
import kr.co.wanted.wantedpreonboardingbackend.errors.exception.BoardIdNotFound;
import kr.co.wanted.wantedpreonboardingbackend.repository.PostRepository;
import kr.co.wanted.wantedpreonboardingbackend.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@WithMockUser(username="test@google.com", password = "12345678")
class PostServiceTest {

    @Mock
    private ModelMapper modelMapper;
    @Mock
    private PostRepository boardRepository;
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private PostServiceImpl boardService;

    private PostDTO boardDTO;
    private MemberJoinDTO memberJoinDTO;
    private Member member;


    @BeforeEach
    void setUp() {
        boardService = new PostServiceImpl(modelMapper, boardRepository, memberRepository);
        boardDTO = PostDTO.builder()
                .id(1L)
                .title("title")
                .content("content")
                .build();

        memberJoinDTO = MemberJoinDTO.builder()
                .email("test@google.com")
                .password("12345678")
                .build();

        member = Member.from(MemberJoinDTO.builder()
                .email("test@google.com")
                .password("12345678")
                .build());
    }

    @Test
    @DisplayName("게시물 등록")
    void register() {
        Optional<Member> member = Optional.of(Member.from(memberJoinDTO));
        Post board = Post.from(boardDTO, member.get());

        when(memberRepository.findByEmail(any(String.class))).thenReturn(member);
        when(boardRepository.save(any(Post.class))).thenReturn(board);

        assertThat(boardService.register(boardDTO)).isEqualTo(board.getId());
    }

    @Test
    @DisplayName("게시물 조회")
    void read() {
        final Post board = Post.from(boardDTO, member);
        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));
        when(modelMapper.map(board, PostDTO.class)).thenReturn(boardDTO);

        assertThat(boardService.read(anyLong())).isEqualTo(boardDTO);
    }

    @Test
    @DisplayName("게시물 수정")
    void update() {
        Post board = Post.from(boardDTO, member);

        PostDTO updateDTO = PostDTO.builder()
                .id(board.getId())
                .title("updateTitle")
                .content("updateContent")
                .build();

        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));
        when(boardRepository.save(any(Post.class))).thenReturn(board);
        when(modelMapper.map(board, PostDTO.class)).thenReturn(updateDTO);

        final PostDTO updatedBoard = boardService.update(anyLong(), updateDTO);

        assertThat(boardDTO.getTitle()).isNotEqualTo(updatedBoard.getTitle());
        assertThat(boardDTO.getContent()).isNotEqualTo(updatedBoard.getContent());
        assertThat(updatedBoard.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("게시물 삭제")
    void delete() {
        final Post board = Post.from(boardDTO, member);

        when(boardRepository.existsById(anyLong())).thenReturn(true);
        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));

        assertThat(boardService.delete(board.getId())).isEqualTo(board.getId());
        verify(boardRepository).deleteById(anyLong());
    }

    @Test
    @DisplayName("조회하는 게시물 번호가 없을 때 발생하는 예외")
    void testBoardIdNotFoundException() {
        final BoardIdNotFound notFound = assertThrows(BoardIdNotFound.class,
                () -> boardService.read(anyLong()));

        final String message = notFound.getErrorCode().getMessage();
        assertThat(message).isEqualTo("Invalid parameter included");
    }
}