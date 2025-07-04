package com.example.data_jpa.repository;

import com.example.data_jpa.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

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

}