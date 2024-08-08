package com.weather.wear.member.repository;

import com.weather.wear.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<Member, Long> {
    // findBy뒤에 컬럼명을 붙여주면 이를 이용한 검색이 가능하다
    public List<Member> findByEmail(String email);

    //like검색도 가능
    public List<Member> findByEmailLike(String keyword);
}
