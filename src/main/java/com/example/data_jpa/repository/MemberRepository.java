package com.example.data_jpa.repository;

import com.example.data_jpa.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 스프링 데이터 JPA는 메소드 이름을 분석해서 JPQL을 생성하고 실행
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    /* 아래는 Spring Data JPA 기능 - 메서드 이름으로 쿼리 생성 */
    List<Member> findBy();

    Long countBy();

    Boolean existsBy();

    Long removeBy();

    List<Member> findTop3By();

    /**
     * NamedQuery
     * NamedQuery가 있을 경우 NamedQuery 먼저 선택됨
     * 명명규칙(Member(타입).NamedQuery) 이름이 같은 경우 @Query 어노테이션 생략 가능
     */
    // @Query(name = "Member.findByUsername")
    List<Member> findByUsername(String username);
}
