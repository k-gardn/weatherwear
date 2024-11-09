package com.weather.wear.board.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;

@Entity(name = "ww_board_images") // JPA가 관리하는 클래스
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 생성자를 통해서 값 변경 목적으로 접근하는 메시지들 차단
public class Images {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("게시글 번호")
    @Column(name="images_ID")
    private Long id;

    @Comment("사용자 지정 파일 이름")
    @Column(nullable = false)
    private String uploadFileName;

    @Comment("저장된 파일 이름")
    @Column(nullable = false)
    private String storedFileName;

    @Comment("파일 저장 경로")
    @Column(nullable = false)
    private String fullPath;

    @Comment("파일 사이즈")
    private Long size;

    @Comment("확장자")
    private String extension;

    @ManyToOne
    @JoinColumn(name = "boardId")
    @JsonBackReference  // 순환 참조 방지
    private Board board;

    public Images(String uploadFileName, String storedFileName, String fullPath,
                  Long size
//                  ,String extension
    ){
        this.uploadFileName = uploadFileName;
        this.storedFileName = storedFileName;
        this.fullPath = fullPath;
        this.size = size;
//        this.extension = extension;
    }

    public void setBoard(Board board){
        this.board = board;
        if(!board.getUploadImages().contains(this)){
            board.getUploadImages().add(this);
        }
    }
}
