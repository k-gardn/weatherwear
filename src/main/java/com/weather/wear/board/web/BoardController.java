package com.weather.wear.board.web;

import com.weather.wear.board.entity.BoardPostRequest;
import com.weather.wear.board.service.BoardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@RestController
public class BoardController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BoardService boardService;

    //게시글 쓰기
    @PostMapping(value = "/create/board",
    consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public Map<String, Object> createBoard(@RequestPart BoardPostRequest request,
                                           @RequestPart("files")MultipartFile[] files) throws IOException {
        // @RequestPart는 HTTP request body에 multipart/form-data 가 포함되어 있는 경우에 사용하는 어노테이션

        Long boardId = boardService.createBoard(request, files);
        Map<String, Object> rstMap = new HashMap<>();
        rstMap.put("success", true);
        return rstMap;
    }

}



