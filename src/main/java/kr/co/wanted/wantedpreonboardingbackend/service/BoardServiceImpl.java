package kr.co.wanted.wantedpreonboardingbackend.service;

import kr.co.wanted.wantedpreonboardingbackend.domain.Member;
import kr.co.wanted.wantedpreonboardingbackend.errors.errorcode.CommonErrorCode;
import kr.co.wanted.wantedpreonboardingbackend.errors.errorcode.CustomErrorCode;
import kr.co.wanted.wantedpreonboardingbackend.errors.errorcode.ErrorCode;
import kr.co.wanted.wantedpreonboardingbackend.errors.exception.BoardIdNotFound;
import kr.co.wanted.wantedpreonboardingbackend.errors.exception.BoardWriterValidation;
import kr.co.wanted.wantedpreonboardingbackend.domain.Board;
import kr.co.wanted.wantedpreonboardingbackend.dto.BoardDTO;
import kr.co.wanted.wantedpreonboardingbackend.repository.BoardRepository;
import kr.co.wanted.wantedpreonboardingbackend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService {

    private final ModelMapper modelMapper;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    /**
     *
     * @param boardDTO
     * @return
     */
    @Override
    public Long register(BoardDTO boardDTO) {

        final String email = authenticationGetUsername();
        final Optional<Member> findMember = memberRepository.findByEmail(email);
        final Member member = findMember.orElseThrow();

        final Board board = modelMapper.map(boardDTO, Board.class);
        board.changeWriter(member);
        final Board result = boardRepository.save(board);

        return result.getId();
    }


    @Transactional(readOnly = true)
    @Override
    public BoardDTO read(Long id) {
        final Optional<Board> result = boardRepository.findById(id);
        final Board board = result.orElseThrow(() -> new BoardIdNotFound(CommonErrorCode.INVALID_PARAMETER));

        return modelMapper.map(board, BoardDTO.class);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<BoardDTO> list(Pageable pageable) {
        pageable.getSort().descending();
        final Page<Board> boardList = boardRepository.findAll(pageable);

        return boardList.map(board -> modelMapper.map(board, BoardDTO.class));
    }

    @Override
    public BoardDTO update(Long id, BoardDTO boardDTO) {
        final Optional<Board> result = boardRepository.findById(id);
        final Board board = result.orElseThrow(() -> new BoardIdNotFound(CustomErrorCode.INVALID_PARAMETER));
        validationWriter(board);
        board.change(boardDTO.getTitle(), boardDTO.getContent());
        boardRepository.save(board);

        return modelMapper.map(board, BoardDTO.class);
    }

    @Override
    public Long delete(Long id) {
        final boolean exists = boardRepository.existsById(id);

        if (!exists) {
            throw new BoardIdNotFound(CommonErrorCode.INVALID_PARAMETER);
        }

        final Optional<Board> result = boardRepository.findById(id);
        final Board board = result.orElseThrow();
        validationWriter(board);

        boardRepository.deleteById(id);

        return id;
    }

    /**
     * 로그인한 사용자가 게시글을 수정할 수 있는 게시글 작성자인지 확인
     * @param board 수정 할 게시물
     */
    private void validationWriter(Board board) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        if (!Objects.equals(email, board.getMember().getEmail())) {
            throw new BoardWriterValidation(CustomErrorCode.WRITER_MISS_MACH);
        }
    }

    /**
     * 현재 로그인한 유저의 email 정보 가져오기
     * @return email
     */
    private String authenticationGetUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
