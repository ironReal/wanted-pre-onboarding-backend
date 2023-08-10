package kr.co.wanted.wantedpreonboardingbackend.domain;

import kr.co.wanted.wantedpreonboardingbackend.dto.PostDTO;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(value = AuditingEntityListener.class)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(length = 20, nullable = false)
    private String title;

    @Column(length = 500, nullable = false)
    private String content;

    private String member;

    @CreatedDate
    @Column(name = "reg_date", updatable = false)
    private LocalDateTime regDate;

    @LastModifiedDate
    @Column(name = "mod_date")
    private LocalDateTime modDate;

    public void change(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void changeWriter(String writer) {
        this.member = writer;
    }

    public static Post from(PostDTO dto) {
        Post board = new Post();
        board.id = dto.getId();
        board.title = dto.getTitle();
        board.content = dto.getContent();

        return board;
    }
}
