package kr.co.wanted.wantedpreonboardingbackend.service;

import kr.co.wanted.wantedpreonboardingbackend.config.CustomSecurityConfig;
import kr.co.wanted.wantedpreonboardingbackend.domain.Board;
import kr.co.wanted.wantedpreonboardingbackend.domain.Member;
import kr.co.wanted.wantedpreonboardingbackend.dto.BoardDTO;
import kr.co.wanted.wantedpreonboardingbackend.dto.MemberJoinDTO;
import kr.co.wanted.wantedpreonboardingbackend.dto.MemberLoginDTO;
import kr.co.wanted.wantedpreonboardingbackend.errors.exception.BoardIdNotFound;
import kr.co.wanted.wantedpreonboardingbackend.repository.BoardRepository;
import kr.co.wanted.wantedpreonboardingbackend.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.webservices.server.AutoConfigureMockWebServiceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@WithMockUser(username="test@google.com", password = "12345678")
class BoardServiceTest {

    @Mock
    private ModelMapper modelMapper;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private BoardServiceImpl boardService;

    private BoardDTO boardDTO;
    private MemberJoinDTO memberJoinDTO;


    @BeforeEach
    void setUp() {
        boardService = new BoardServiceImpl(modelMapper, boardRepository, memberRepository);
        boardDTO = BoardDTO.builder()
                .id(1L)
                .title("title")
                .content("content")
                .build();

        memberJoinDTO = MemberJoinDTO.builder()
                .email("test@google.com")
                .password("12345678")
                .build();
    }

    @Test
    @DisplayName("게시물 등록")
    void register() {
        Optional<Member> member = Optional.of(Member.from(memberJoinDTO));
        Board board = Board.from(boardDTO);

        when(memberRepository.findByEmail(any(String.class))).thenReturn(member);
        when(modelMapper.map(boardDTO, Board.class)).thenReturn(board);
        when(boardRepository.save(any(Board.class))).thenReturn(board);

        assertThat(boardService.register(boardDTO)).isEqualTo(board.getId());
    }

    @Test
    @DisplayName("게시물 조회")
    void read() {
        final Board board = Board.from(boardDTO);
        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));
        when(modelMapper.map(board, BoardDTO.class)).thenReturn(boardDTO);

        assertThat(boardService.read(anyLong())).isEqualTo(boardDTO);
    }

    @Test
    @DisplayName("게시물 수정")
    void update() {
        Board board = Board.from(boardDTO);
        board.changeWriter(Member.from(memberJoinDTO));

        BoardDTO updateDTO = BoardDTO.builder()
                .id(board.getId())
                .title("updateTitle")
                .content("updateContent")
                .build();

        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));
        when(boardRepository.save(any(Board.class))).thenReturn(board);
        when(modelMapper.map(board, BoardDTO.class)).thenReturn(updateDTO);

        final BoardDTO updatedBoard = boardService.update(anyLong(), updateDTO);

        assertThat(boardDTO.getTitle()).isNotEqualTo(updatedBoard.getTitle());
        assertThat(boardDTO.getContent()).isNotEqualTo(updatedBoard.getContent());
        assertThat(updatedBoard.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("게시물 삭제")
    void delete() {
        final Board board = Board.from(boardDTO);
        board.changeWriter(Member.from(memberJoinDTO));

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