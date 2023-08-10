package kr.co.wanted.wantedpreonboardingbackend.domain;

import kr.co.wanted.wantedpreonboardingbackend.dto.MemberJoinDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String password;

    @OneToMany(mappedBy = "member")
    private List<Post> postList = new ArrayList<>();

    public void changePassword(String password) {
        this.password = password;
    }

    public static Member from(MemberJoinDTO dto) {
        Member member = new Member();
        member.email = dto.getEmail();
        member.password = dto.getPassword();

        return member;
    }
}
