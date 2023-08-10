package kr.co.wanted.wantedpreonboardingbackend.controller;

import kr.co.wanted.wantedpreonboardingbackend.dto.PostDTO;
import kr.co.wanted.wantedpreonboardingbackend.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService boardService;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> register(@RequestBody PostDTO boardDTO) {
        log.info("BOARD REGISTER CONTROLLER");
        final Long id = boardService.register(boardDTO);
        Map<String, Long> resultMap = Map.of("created board id", id);

        return new ResponseEntity<>(resultMap, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PostDTO> read(@PathVariable Long id) {
        log.info("BOARD READ CONTROLLER");
        final PostDTO findBoard = boardService.read(id);

        return new ResponseEntity<>(findBoard, HttpStatus.OK);
    }

    @GetMapping(value = "/list")
    public ResponseEntity<Page<PostDTO>> list(Pageable pageable) {
        log.info("BOARD LIST CONTROLLER");
        final Page<PostDTO> list = boardService.list(pageable);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostDTO> update(
            @PathVariable Long id,
            @RequestBody PostDTO boardDTO) {
        log.info("BOARD UPDATE CONTROLLER");

        final PostDTO updateBoard = boardService.update(id, boardDTO);

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
