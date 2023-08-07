package kr.co.wanted.wantedpreonboardingbackend.controller;

import kr.co.wanted.wantedpreonboardingbackend.dto.BoardDTO;
import kr.co.wanted.wantedpreonboardingbackend.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> register(@RequestBody BoardDTO boardDTO) {
        log.info("BOARD REGISTER CONTROLLER");
        final Long id = boardService.register(boardDTO);
        Map<String, Long> resultMap = Map.of("created board id", id);

        return new ResponseEntity<>(resultMap, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<BoardDTO> read(@PathVariable Long id) {
        log.info("BOARD READ CONTROLLER");
        final BoardDTO findBoard = boardService.read(id);

        return new ResponseEntity<>(findBoard, HttpStatus.OK);
    }

    @GetMapping(value = "/list")
    public ResponseEntity<Page<BoardDTO>> list(Pageable pageable) {
        log.info("BOARD LIST CONTROLLER");
        final Page<BoardDTO> list = boardService.list(pageable);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BoardDTO> update(
            @PathVariable Long id,
            @RequestBody BoardDTO boardDTO) {
        log.info("BOARD UPDATE CONTROLLER");

        final BoardDTO updateBoard = boardService.update(id, boardDTO);

        return new ResponseEntity<>(updateBoard, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Map<String, Long>> delete(@PathVariable Long id) {
        log.info("BOARD DELETE CONTROLLER");
        final Long deleteId = boardService.delete(id);
        Map<String, Long> resultMap = Map.of("delete_board_id", deleteId);

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }
}
