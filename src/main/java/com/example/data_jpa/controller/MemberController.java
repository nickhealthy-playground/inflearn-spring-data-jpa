package com.example.controller;

import com.example.data_jpa.entity.Member;
import com.example.data_jpa.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    // 도메인 컨버터 기능 X
    @GetMapping("/members1/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    // 도메인 컨버터 기능 - 도메인 클래스 컨버터도 리포지토리를 사용해서 엔티티를 찾음(사용하더라도 '조회용'으로만 사용)
    // PK를 통해 데이터를 조회하므로 스프링 데이터 JPA가 자동으로 member 엔티티 객체를 조회해서 반환해준다.
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    /**
     * 페이징과 정렬
     * - 스프링 데이터 JPA가 제공하는 페이징과 정렬 기능을 스프링 MVC에서 편리하게 사용 가능
     * - 파라미터로 Pageable 인터페이스를 받고, 스프링이 실제로 PageRequest 객체를 생성 및 설정(페이징 및 정렬)
     */
    @GetMapping("/members")
    public Page<Member> findMembers(Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        return page;
    }

    @PostConstruct
    public void init() {
        memberRepository.save(new Member("member"));
    }
}
