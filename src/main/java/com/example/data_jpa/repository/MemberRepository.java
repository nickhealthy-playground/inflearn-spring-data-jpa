package com.example.data_jpa.repository;

import com.example.data_jpa.dto.MemberDto;
import com.example.data_jpa.entity.Member;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

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

    // 컬렉션 파라미터 바인딩
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    // 다양한 반환타입 지원
    List<Member> findListByUsername(String name); //컬렉션
    Member findMemberByUsername(String name); //단건
    Optional<Member> findOptionalByUsername(String name); //단건 Optional

    /**
     * 페이징 처리(다양한 반환 타입 지원)
     * @Query: count 쿼리 분리 가능 -> join이 있어도 count 개수가 변함없는 경우 join 없이 카운터 하는 것이 성능상 유리하므로 카운트 쿼리를 최적화함
     */
//    @Query(value = "select m from Member m left join m.team t",
//            countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);
//    Slice<Member> findByAge(int age, Pageable pageable);
//    List<Member> findByAge(int age, Pageable pageable);

    /**
     * 벌크성 수정 쿼리
     * @Modifying
     * - Spring DATA JPA에서는 executeUpdate() 메서드를 지원하지 않기 때문에 해당 어노테이션이 필요함
     * - clearAutomatically: 영속성 컨텍스트 자동 초기화(해당 쿼리 실행 후 영속성 컨텍스트를 자동으로 초기화함)
     */
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    /**
     * EntityGraph
     * 사실상 페치 조인(FETCH JOIN)의 간편 버전
     * LEFT OUTER JOIN 사용
     */
    // 공통 메서드 오버라이딩
    @Override
    @EntityGraph(attributePaths = ("team"))
    List<Member> findAll();

    // JPQL + 엔티티 그래프
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m left join fetch m.team t")
    List<Member> findMemberEntityGraph();

    // 메서드 이름으로 쿼리
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    // JPA 표준 - @NamedEntityGraph
    @EntityGraph(value = "Member.all")
    @Query("select m from Member m")
    List<Member> findMemberNamedEntityGraph();

    /**
     * JPA 쿼리 힌트 사용
     * - 해당 메서드를 사용하면 엔티티를 읽기 전용으로 번경
     */
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"), forCounting = true)
    Page<Member> findQueryHintsByUsername(String username, Pageable pageable);

    /**
     * Projections
     */
    List<UsernameOnly> findProjectionsInterfaceByUsername(@Param("username") String username);
    List<UsernameOnlyDto> findProjectionsClassByUsername(@Param("username") String username);
    <T> List<T> findProjectionsDynamicByUsername(@Param("username") String username, Class<T> type);
}
