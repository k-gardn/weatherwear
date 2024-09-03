package com.weather.wear.member.domain;

import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class token {

    @Column(name = "user_no", nullable = false)
    private Long userNo;

}
