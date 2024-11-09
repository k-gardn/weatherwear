package com.weather.wear.board.repository;

import com.weather.wear.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // Member 엔티티의 이메일을 기준으로 해당 사용자의 게시글만 조회
    List<Board> findByMemberEmail(String email);

    // 사용자 이메일과 날짜로 게시글을 조회하는 쿼리
//    @Query("SELECT b FROM Board b WHERE b.userEmail = :userEmail AND b.createdDate = :date")
//    List<Board> findByUserEmailAndDate(@Param("userEmail") String userEmail, @Param("date") LocalDate date);

    // Member의 email을 기준으로 게시글을 조회하고, 게시글이 저장된 날짜로 필터링
    List<Board> findByMemberEmailAndCreatedTime(String userEmail, LocalDate createdDate);

}
