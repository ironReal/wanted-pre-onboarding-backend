package kr.co.wanted.wantedpreonboardingbackend.domain;

import kr.co.wanted.wantedpreonboardingbackend.dto.BoardDTO;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(value = AuditingEntityListener.class)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(length = 20, nullable = false)
    private String title;

    @Column(length = 500, nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @CreatedDate
    @Column(name = "regDate", updatable = false)
    private LocalDateTime regDate;

    @LastModifiedDate
    @Column(name = "modDate")
    private LocalDateTime modDate;

    public void change(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void changeWriter(Member writer) {
        this.member = writer;
    }

    public static Board from(BoardDTO dto) {
        Board board = new Board();
        board.id = dto.getId();
        board.title = dto.getTitle();
        board.content = dto.getTitle();

        return board;
    }
}
