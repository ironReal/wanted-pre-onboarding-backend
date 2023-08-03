package kr.co.wanted.wantedpreonboardingbackend.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberJoinDTO {

    @Email(message = "Email format is not correct")
    private String email;

    @Length(min = 8, message = "Password must be at least 8 characters long")
    private String password;
}
