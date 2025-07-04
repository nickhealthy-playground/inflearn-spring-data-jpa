package com.example.data_jpa.repository;

import com.example.data_jpa.dto.MemberDto;
import com.example.data_jpa.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * 이름 없는 Named 쿼리
     * - 실행할 메서드에 정적 쿼리를 직접 작성
     * - JPA Named 쿼리처럼 애플리케이션 실행 시점에 문법 오류를 발견할 수 있음(매우 큰 장점!)
     */
    @Query("select m from Member m where m.username= :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    // 단순히 값 하나를 조회
    @Query("select m.username from Member m")
    List<String> findUsernamesList();

    // DTO로 직접 조회
    @Query("select new com.example.data_jpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();
}
