package com.weather.wear.member.repository;

import com.weather.wear.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//DAO와 같은 역할
public interface UserRepository extends JpaRepository<Member, Long> {
    // findBy뒤에 컬럼명을 붙여주면 이를 이용한 검색이 가능하다

    //like검색도 가능
    List<Member> findByEmailLike(String keyword);

    //로그인시 이메일로 회원 찾기
    Member findByEmail(String email);

    //회원가입
}
