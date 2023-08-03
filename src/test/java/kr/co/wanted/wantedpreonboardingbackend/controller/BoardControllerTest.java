package kr.co.wanted.wantedpreonboardingbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.wanted.wantedpreonboardingbackend.dto.BoardDTO;
import kr.co.wanted.wantedpreonboardingbackend.service.BoardService;
import kr.co.wanted.wantedpreonboardingbackend.util.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BoardService boardService;

    @InjectMocks
    private JWTUtil jwtUtil;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "key", "12341234");
    }


    @Test
    @DisplayName("게시물 등록")
    void registerTest() throws Exception {
        final BoardDTO DTO = BoardDTO.builder()
                .title("testTitle")
                .content("testContent")
                .build();

        Map<String, Object> claim = Map.of("email", "test@google.com");
        final String token = jwtUtil.generatedToken(claim, 1);

        when(boardService.register(DTO)).thenReturn(1L);

        mvc.perform(MockMvcRequestBuilders.post("/api/")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(DTO))
                        .with(csrf()))
                .andExpect(status().isCreated());
    }

}