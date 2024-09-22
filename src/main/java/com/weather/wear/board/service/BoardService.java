package com.weather.wear.board.service;

import com.weather.wear.board.entity.Board;
import com.weather.wear.board.entity.BoardPostRequest;
import com.weather.wear.board.entity.Images;
import com.weather.wear.board.repository.BoardRepository;
import com.weather.wear.board.repository.ImageRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Service
public class BoardService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    ImageRepository imageRepository;

    @Value("${location}")
    private String fileDir;

    @Transactional
    public Long createBoard(BoardPostRequest request,
                            MultipartFile[] multipartFile ) throws IOException{
        Board board = new Board(request.getTitle(), request.getContents());
        Long id = boardRepository.save(board).getId();

        log.info("multipartFile = {}", (Object) multipartFile);
        if(!Objects.isNull(multipartFile)){
            for (MultipartFile file : multipartFile){
                String originalFilename = file.getOriginalFilename();
                log.info("originalFilename = {}", originalFilename);

                Long size = file.getSize();
                log.info("size = {}", size);

                String contentType = file.getContentType();
                log.info("contentType = {}", contentType);

                String fullPath = fileDir + originalFilename;
                log.info("fullPath = {}", fullPath);

                Images images = new Images(originalFilename, originalFilename, fullPath, size);
                images.setBoard(board);
                imageRepository.save(images);

                file.transferTo(new File(fullPath));
            }
        }
        return id;
    }

}
