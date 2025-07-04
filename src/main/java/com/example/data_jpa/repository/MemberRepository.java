package com.example.data_jpa.repository;

import com.example.data_jpa.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 스프링 데이터 JPA는 메소드 이름을 분석해서 JPQL을 생성하고 실행
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findBy();

    Long countBy();

    Boolean existsBy();

    Long removeBy();

    List<Member> findTop3By();
}
