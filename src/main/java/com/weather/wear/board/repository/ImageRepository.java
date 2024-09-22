package com.weather.wear.board.repository;

import com.weather.wear.board.entity.Board;
import com.weather.wear.board.entity.Images;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Images, Long> {


}
