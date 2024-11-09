package com.weather.wear.board.service;

import com.weather.wear.board.entity.Board;
import com.weather.wear.board.entity.BoardPostRequest;
import com.weather.wear.board.entity.Images;
import com.weather.wear.board.repository.BoardRepository;
import com.weather.wear.board.repository.ImageRepository;
import com.weather.wear.member.domain.Member;
import com.weather.wear.member.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class BoardService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRepository memberRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    ImageRepository imageRepository;

    @Value("${location}")
    private String fileDir;

    @Transactional
    public Long createBoard(BoardPostRequest request,
                            MultipartFile[] multipartFile ) throws IOException{
//        Board board = new Board(request.getTitle(), request.getContents());

//        Long id = boardRepository.save(board).getId();

        String email = request.getEmail();  // 요청에서 이메일 가져오기
        Member member = memberRepository.findByEmail(email);  // 이메일로 회원 조회

        Board board = Board.builder()
                .title(request.getTitle())
                .contents(request.getContents())
                .member(member)  // 회원 정보 설정
                .build();

        Long id = boardRepository.save(board).getId();  // 게시글 db 저장

        log.info("multipartFile = {}", (Object) multipartFile);
        if(multipartFile != null){
            for (MultipartFile file : multipartFile){
                String originalFilename = file.getOriginalFilename();
//                log.info("originalFilename = {}", originalFilename);
                Long size = file.getSize();
//                log.info("size = {}", size);
                String contentType = file.getContentType();
//                log.info("contentType = {}", contentType);
                String fullPath = fileDir + originalFilename;
                log.info("fullPath = {}", fullPath);
                Images images = new Images(originalFilename, originalFilename, fullPath, size);
                images.setBoard(board);
                imageRepository.save(images);   //이미지 db에 저장

                file.transferTo(new File(fullPath));    //이미지 서버에 저장(현재 로컬에 저장)
            }
        }
        return id;
    }


    // 게시글 수정
    @Transactional
    public Board updatePost(Long postId, BoardPostRequest request,
                           MultipartFile[] multipartFile){
        Board board = boardRepository.findById(postId).orElseThrow(()-> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        board.setTitle(request.getTitle());
        board.setContents( request.getContents());
        // 수정 시간을 현재 날짜로 설정 (시간 정보는 포함되지 않음)
        board.setUpdatedTime(LocalDate.now());  // LocalDate로 설정 // 수정 시간 설정
        return board;
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId){
        boardRepository.deleteById(postId);
    }

    // 게시글 전체보기
    @Transactional
    public List<Board> getAllBoards(){
        return boardRepository.findAll();
    }

    // 특정 사용자 게시글 조회
    @Transactional
    public List<Board> getBoardsByUserEmail(String email) {
        return boardRepository.findByMemberEmail(email);
    }

    //프론트에서 다 불러오고 날짜별 필터링하면, 호출 수를 좀 줄일 수 있긴함!..
    @Transactional
    public List<Board> getBoardsByUserEmailAndDate(String userEmail, String date) {
        // 날짜를 LocalDate로 변환한 후 Timestamp로 변환
        LocalDate localDate = LocalDate.parse(date);
//        Timestamp timestamp = Timestamp.valueOf(localDate.atStartOfDay()); // 해당 날짜의 시작 시간을 기준으로 생성
        return boardRepository.findByMemberEmailAndCreatedTime(userEmail, localDate);
    }

}
