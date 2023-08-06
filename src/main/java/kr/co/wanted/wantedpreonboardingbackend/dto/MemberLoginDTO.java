package kr.co.wanted.wantedpreonboardingbackend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.validation.constraints.NotEmpty;
import java.util.Collection;

@Getter
@Setter
@ToString
public class MemberLoginDTO extends User {

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;


    public MemberLoginDTO(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.username = username;
        this.password = password;
    }
}
