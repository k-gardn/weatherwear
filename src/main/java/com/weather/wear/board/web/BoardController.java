package com.weather.wear.board.web;

import com.weather.wear.board.entity.Board;
import com.weather.wear.board.entity.BoardPostRequest;
import com.weather.wear.board.service.BoardService;
import com.weather.wear.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/boards")
public class BoardController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BoardService boardService;


    // 모든 회원 글 조회
    @GetMapping(value = "/getAllBoards", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<List<Board>> getAllBoards() {
        List<Board> board
                = boardService.getAllBoards();
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    //게시글 쓰기
    @PostMapping(value = "/create",
    consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public Map<String, Object> createBoard(@RequestPart BoardPostRequest request,
                                           @RequestPart("files")MultipartFile[] files) throws IOException {
        // @RequestPart는 HTTP request body에 multipart/form-data 가 포함되어 있는 경우에 사용하는 어노테이션

        Long boardId = boardService.createBoard(request, files);
        Map<String, Object> rstMap = new HashMap<>();
        rstMap.put("success", true);
        return rstMap;
    }

    // 게시글 업데이트
    @PutMapping(value = "/{boardId}", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Board> updateBoard(@PathVariable Long boardId, @RequestBody BoardPostRequest request,
                                             @RequestPart("files")MultipartFile[] files) {
        Board updatedBoard = boardService.updatePost(boardId, request, files);  // 게시글 업데이트
        return new ResponseEntity<>(updatedBoard, HttpStatus.OK);
    }

    // 게시글 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {
        boardService.deletePost(boardId);  // 게시글 삭제
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 본인의 게시글 전체 및 날짜별 조회
    @PostMapping("/my-posts")
    public Map<String, Object> getMyBoards(HttpServletRequest request,
                                           @RequestBody(required = false) BoardPostRequest boardPostRequest) {
        Map<String, Object> rstMap = new HashMap<>();

        // Interceptor에서 설정한 사용자 이메일 가져오기
        String userEmail = (String) request.getAttribute("userEmail");
        
        // 날짜 파라미터가 제공되지 않으면 전체 게시글을 조회
        if (boardPostRequest == null || boardPostRequest.getDate() == null || boardPostRequest.getDate().isEmpty()) {
            rstMap.put("data", boardService.getBoardsByUserEmail(userEmail));
        } else {
            // 날짜가 제공되면 해당 날짜의 게시글만 조회
            rstMap.put("data", boardService.getBoardsByUserEmailAndDate(userEmail, boardPostRequest.getDate()));
        }

        rstMap.put("success", true);
        return rstMap;
    }



}



