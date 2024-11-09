package com.weather.wear.board.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.weather.wear.member.domain.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "ww_board") // JPA가 관리하는 클래스
@Data
@NoArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("게시글 번호")
    @Column(name="Board_ID")
    private Long id;

    @Comment("게시글 제목")
    @Column(nullable = false)
    private String title;

    @Comment("게시글 내용")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contents;

    @CreationTimestamp
    @Column(name = "saved_date", updatable = false)
    private LocalDate createdTime;

    @CreationTimestamp
    @Column(name = "updated_date")
    private LocalDate  updatedTime;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    @JsonManagedReference  // 순환 참조 방지
    private List<Images> uploadImages = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "user_email")  // 수정된 부분
    private Member member;

    @Builder
    public Board(String title, String contents, Member member, LocalDate  createdTime, LocalDate updatedTime) {
        this.title = title;
        this.contents = contents;
        this.member = member;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }

    public void updateBoard(String title, String contents, LocalDate updatedTime){
        this.title = title;
        this.contents = contents;
        this.updatedTime = updatedTime;
    }

    public void deleteBoard(String title, String contents){
        this.title = title;
        this.contents = contents;
    }

    public void createImages(Images images){
        this.uploadImages.add(images);
        if(images.getBoard() != this){
            images.setBoard(this);
        }
    }

}
