package com.weather.wear.board.entity;

import lombok.Data;

@Data
public class BoardPostRequest{
    private String title;
    private String contents;
    private String email; // 사용자 이메일 추가
    private String date;

}
