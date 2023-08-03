package kr.co.wanted.wantedpreonboardingbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.wanted.wantedpreonboardingbackend.config.CustomSecurityConfig;
import kr.co.wanted.wantedpreonboardingbackend.dto.MemberJoinDTO;
import kr.co.wanted.wantedpreonboardingbackend.service.MemberService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.print.attribute.standard.Media;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@ExtendWith(SpringExtension.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private MemberService memberService;


    @Test
    @WithMockUser
    @DisplayName("회원가입 정상 동작 확인")
    void signUpTest() throws Exception {
        final MemberJoinDTO memberJoinDTO = MemberJoinDTO.builder()
                .email("test@google.com")
                .password("12341234")
                .build();

        when(memberService.join(memberJoinDTO)).thenReturn(memberJoinDTO.getEmail());

        mvc.perform(MockMvcRequestBuilders.post("/api/member/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(memberJoinDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());


    }

    @Test
    @WithMockUser
    @DisplayName("회원가입 시 이메일 검증")
    void signUpEmailValidationTest() throws Exception {
        final MemberJoinDTO memberJoinDTO = MemberJoinDTO.builder()
                .email("testgoogle.com")
                .password("test1234")
                .build();

        mvc.perform(post("/api/member/join")
                        .content(mapper.writeValueAsString(memberJoinDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email format is not correct"));

    }

    @Test
    @WithMockUser
    @DisplayName("회원가입 시 비밀번호 검증")
    void signUpPasswordValidationTest() throws Exception {
        final MemberJoinDTO memberJoinDTO = MemberJoinDTO.builder()
                .email("test@google.com")
                .password("")
                .build();

        mvc.perform(post("/api/member/join")
                        .content(mapper.writeValueAsString(memberJoinDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Password must be at least 8 characters long"));

    }

}