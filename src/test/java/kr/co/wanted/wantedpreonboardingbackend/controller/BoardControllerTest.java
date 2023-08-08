package kr.co.wanted.wantedpreonboardingbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.wanted.wantedpreonboardingbackend.config.CustomSecurityConfig;
import kr.co.wanted.wantedpreonboardingbackend.dto.BoardDTO;
import kr.co.wanted.wantedpreonboardingbackend.repository.BoardRepository;
import kr.co.wanted.wantedpreonboardingbackend.repository.MemberRepository;
import kr.co.wanted.wantedpreonboardingbackend.service.BoardService;
import kr.co.wanted.wantedpreonboardingbackend.util.JWTUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@WithMockUser(username = "test@google.com", password = "12345678")
@WebMvcTest(value = BoardController.class,
excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomSecurityConfig.class))
class BoardControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BoardService boardService;

    @InjectMocks
    private JWTUtil jwtUtil;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private BoardRepository boardRepository;

    private BoardDTO boardDTO;
    private final Long ID = 1L;


    @BeforeEach
    void setUp() {
        boardDTO = BoardDTO.builder()
                .id(1L)
                .title("testTitle")
                .content("testContent")
                .build();

        ReflectionTestUtils.setField(jwtUtil, "key", "12341234");
    }


    @Test
    @DisplayName("게시물 등록")
    void testBoardRegister() throws Exception {
        when(boardService.register(boardDTO)).thenReturn(1L);

        mvc.perform(MockMvcRequestBuilders.post("/api/board/register")
                        .header(HttpHeaders.AUTHORIZATION, generatedBearerToken(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(boardDTO))
                        .with(csrf()))
                .andExpect(status().isCreated());
        verify(boardService).register(boardDTO);
    }

    @Test
    @DisplayName(("게시물 조회"))
    void testBoardReadId() throws Exception {
        when(boardService.read(ID)).thenReturn(boardDTO);

        mvc.perform(MockMvcRequestBuilders.get("/api/board/{id}", ID)
                        .header(HttpHeaders.AUTHORIZATION, generatedBearerToken(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID));

        verify(boardService).read(ID);
    }

    @Test
    @DisplayName("게시물 수정")
    void testBoardUpdate() throws Exception {
        BoardDTO updateDTO = BoardDTO.builder()
                .id(ID)
                .title("updateTitle")
                .content("updateContent")
                .build();

        when(boardService.update(anyLong(), any(BoardDTO.class))).thenReturn(updateDTO);

        mvc.perform(MockMvcRequestBuilders.put("/api/board/update/{id}", ID)
                        .header(HttpHeaders.AUTHORIZATION, generatedBearerToken(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDTO))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updateDTO.getTitle()))
                .andExpect(jsonPath("$.content").value(updateDTO.getContent()));
    }
    
    @Test
    @DisplayName("게시물 삭제")
    void testBoardRead() throws Exception {
        Long anyId = anyLong();
        when(boardService.delete(anyId)).thenReturn(anyId);

        mvc.perform(MockMvcRequestBuilders.delete("/api/board/{id}", ID)
                        .header(HttpHeaders.AUTHORIZATION, generatedBearerToken(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.delete_board_id").value(anyId));
    }

    private String generatedBearerToken(int day) {
        Map<String, Object> claim = Map.of("email", "test@google.com");
        final String token = jwtUtil.generatedToken(claim, day);

        return "Bearer " + token;
    }

}