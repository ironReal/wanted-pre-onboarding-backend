package kr.co.wanted.wantedpreonboardingbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {

    private Long id;

    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
}
