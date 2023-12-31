package kr.co.wanted.wantedpreonboardingbackend.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {

    private Long id;

    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
}
