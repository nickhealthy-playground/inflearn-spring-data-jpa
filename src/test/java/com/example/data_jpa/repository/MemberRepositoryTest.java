package com.example.data_jpa.repository;

import com.example.data_jpa.dto.MemberDto;
import com.example.data_jpa.entity.Member;
import com.example.data_jpa.entity.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        // given
        Member member = new Member("memberA");

        // when
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        // then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member); //JPA 엔티티 동일성 보장
    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        Long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);

    }

    /**
     * Spring DATA JPA - 메소드 이름으로 쿼리 생성
     * 특정 규칙에 따라 인터페이스 구현 없이 선언만 해도 쿼리를 생성할 수 있다.
     */
    @Test
    void findByUsernameAndAgeGreaterThan() {
        //given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        //when
        memberRepository.save(m1);
        memberRepository.save(m2);

        //then
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    // 스프링 데이터 JPA가 제공하는 쿼리 메소드 기능
    @Test
    void functionSpringDataJPA() {
        Member member1 = new Member("userA");
        memberRepository.save(member1);

        Member member2 = new Member("userB");
        memberRepository.save(member2);

        Boolean b = memberRepository.existsBy();
        System.out.println("b = " + b);

        List<Member> memberList = memberRepository.findTop3By();
        System.out.println("memberList = " + memberList);

        Long removedCount = memberRepository.removeBy();
        System.out.println("removedCount = " + removedCount);

        Long count = memberRepository.countBy();
        System.out.println("count = " + count);

        Boolean b2 = memberRepository.existsBy();
        System.out.println("b2 = " + b2);
    }

    @Test
    void namedQuery() {
        //given
        Member member1 = new Member("AAA");
        Member member2 = new Member("AAA");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        List<Member> aaa = memberRepository.findByUsername("AAA");

        //then
        assertThat(aaa.size()).isEqualTo(2);
        assertThat(aaa.get(0).getUsername()).isEqualTo("AAA");
        assertThat(aaa.get(1).getUsername()).isEqualTo("AAA");
    }

    @Test
    void testQuery() {
        //given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        List<Member> aaa = memberRepository.findUser("AAA", 20);

        //then
        assertThat(aaa.size()).isEqualTo(1);
        assertThat(aaa.get(0).getUsername()).isEqualTo("AAA");
        assertThat(aaa.get(0).getAge()).isEqualTo(20);
    }

    // 단순히 값 하나를 조회
    @Test
    void findUsernamesList() {
        Member member1 = new Member("AAA");
        Member member2 = new Member("BBB");
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> usernamesList = memberRepository.findUsernamesList();
        for (String s : usernamesList) {
            System.out.println("s = " + s);
        }
    }

    // DTO로 직접 조회
    @Test
    void findMemberDto() {
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member member = new Member("AAA");
        member.changeTeam(teamA);
        memberRepository.save(member);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    // 컬렉션 파라미터 바인딩
    @Test
    void findByNames() {
        Member member1 = new Member("AAA");
        Member member2 = new Member("BBB");

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> byNames = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member byName : byNames) {
            System.out.println("byName = " + byName);
        }
    }

    /**
     * JPA vs Spring DATA JPA
     * JPA
     * - JPA에서는 단건 조회(getSingleResult) 시 조회가 되지 않으면 javax.persistence.NonUniqueResultException 발생
     * Spring DATA JPA
     * - 단건 조회 시 조회 되지 않으면 NULL 반환
     * - 에러 발생 시 Spring 에러로 감싸서 반환해줌
     */
    @Test
    void returnType() {
        Member member = new Member("AAA");
        memberRepository.save(member);

        // 리스트로 조회
        List<Member> aaa = memberRepository.findListByUsername("AAA");
        System.out.println("aaa = " + aaa);

        // 단건 조회
        Member m1 = memberRepository.findMemberByUsername("AAA");
        System.out.println("m1 = " + m1);

        // Optional 조회
        Optional<Member> m2 = memberRepository.findOptionalByUsername("AAA");
        System.out.println("m2 = " + m2);
    }

    /**
     * 페이징 조건과 정렬 조건 설정
     * - 정렬 조건: 이름으로 내림차순
     * - 페이징 조건: 첫 번째 페이지, 페이지당 보여줄 데이터는 3건
     */
    @Test
    void page() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        //when

        /*
        * 다양한 반환 타입 지원
        * - Page: 페이징 처리(count 쿼리가 자동으로 발생)
        * - Slice: 지정한 컨텐츠 개수 + 1 데이터만큼 가져옴(count 쿼리를 날리지 않고, 추가 데이터가 존재할 수 있는지 파악하는 용도)
        *   - 주로 '더보기..' 기능을 구현할 때 사용
        * - List: 지정한 컨텐츠 개수만 가져옴
        * */
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        Page<Member> page = memberRepository.findByAge(10, pageRequest);
//        Slice<Member> page = memberRepository.findByAge(10, pageRequest);
//        List<Member> page = memberRepository.findByAge(10, pageRequest);

        // DTO로 변환(엔티티로 반환하면 안되므로 DTO로 변환 필요)
        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));
        System.out.println("toMap = " + toMap);

        //then
        List<Member> content = page.getContent();
        assertThat(content.size()).isEqualTo(3); // 조회된 데이터 수
        assertThat(page.getNumber()).isEqualTo(0); // 현재 페이지
        assertThat(page.getTotalPages()).isEqualTo(2); // 총 페이지 수
        assertThat(page.getTotalElements()).isEqualTo(5); // 총 데이터 개수
        assertThat(page.isFirst()).isTrue(); // 첫 번째 페이지 확인
        assertThat(page.hasNext()).isTrue(); // 다음 페이지 존재 확인
    }

    /**
     * Spring DATA JPA 사용한 벌크성 수정 쿼리 테스트
     * @Modifying 어노테이션 사용 필요
     * - 필요시 영속성 컨텍스트 초기화 옵션(clearAutomatically) 사용 설정
     * <중요>JPA 벌크성 쿼리 시 주의점
     * - JPA는 기본적으로 영속성 컨텍스트를 사용하기 때문에 벌크성 연산처럼 영속성 컨텍스트를 거치지 않고,
     * - DB에 직접 import하는 경우 데이터 정합성 문제 가능성 존재함
     * - 따라서 벌크 연산 이후 추가 작업이 필요한 경우 영속성 컨텍스트를 초기화 후 영속 데이터를 다뤄야 함.
     */
    @Test
    void bulkUpdate() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);

        // then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    void findMembers() {
        //given
        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1");
        Member member2 = new Member("member1");
        member1.changeTeam(teamA);
        member2.changeTeam(teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findMemberNamedEntityGraph();
//        List<Member> members = memberRepository.findAll();
//        List<Member> members = memberRepository.findMemberEntityGraph();
//        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        //then
        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }


    }

    /**
     * JPA 쿼리 힌트 사용
     */
    @Test
    void queryHint() {
        //given
        memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        Member member = memberRepository.findReadOnlyByUsername("member1");
        member.setUsername("member2");

        //then
        em.flush(); // update query 실행 x
    }

    /**
     * JPA 쿼리 힌트 Page 사용
     */
    @Test
    void findQueryHintsByUsername() {
        //given
        memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        Page<Member> page = memberRepository.findQueryHintsByUsername("member1", pageRequest);

        //then
        em.flush();

        List<Member> content = page.getContent();
        System.out.println("content.size() = " + content.size());
        System.out.println("page.getTotalElements() = " + page.getTotalElements());
        System.out.println("page.getTotalPages() = " + page.getTotalPages());
    }

    @Test
    void memberRepositoryCustom() {
        List<Member> memberCustom = memberRepository.findMemberCustom(); // MemberRepositoryImpl에 구현된 메서드 호출됨
    }

    /**
     * 나머지 기능들 - Projections
     * 1. 엔티티 대신 DTO를 편리하게 조회할 때 사용
     * 2. 조회할 엔티티의 필드를 getter 형식으로 지정하면 해당 필드만 선택해서 조회(Projection)
     * @See: com.example.data_jpa.repository.UsernameOnly
     * 3. 인터페이스, 클래스 기반 Projections 생성 가능
     * - 인터페이스 기반: 프로퍼티 형식(getter)의 인터페이스를 제공하면, 구현체는 스프링 데이터 JPA가 제공
     * - 클래스 기반: 생성자의 파라미터 이름으로 매칭
     */
    @Test
    void projections() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        /*인터페이스 기반 Projections*/
        List<UsernameOnly> findMembers1 = memberRepository.findProjectionsInterfaceByUsername("m1");
        for (UsernameOnly findMember : findMembers1) {
            System.out.println("findMember = " + findMember.getUsername());
        }

        /*클래스 기반 Projections*/
        List<UsernameOnlyDto> findMembers2 = memberRepository.findProjectionsClassByUsername("m1");
        for (UsernameOnlyDto findMember : findMembers2) {
            System.out.println("findMember = " + findMember.getUsername());
        }

        /*동적 Projections*/
        List<UsernameOnlyDto> dynamicProjects = memberRepository.findProjectionsDynamicByUsername("m1", UsernameOnlyDto.class);
        for (UsernameOnlyDto dynamicProject : dynamicProjects) {
            System.out.println("dynamicProject = " + dynamicProject.getUsername());
        }


        //then
        assertThat(findMembers1.size()).isEqualTo(1);
        assertThat(findMembers2.size()).isEqualTo(1);
        assertThat(dynamicProjects.size()).isEqualTo(1);

    }

}