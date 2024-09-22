package com.weather.wear.board.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

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

    @OneToMany(mappedBy = "board")
    private List<Images> uploadImages = new ArrayList<>();

    public Board(String title, String contents){
        this.title = title;
        this.contents = contents;
    }

    public void updateBoard(String title, String contents){
        this.title = title;
        this.contents = contents;
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
