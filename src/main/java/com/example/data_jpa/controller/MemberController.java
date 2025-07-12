package com.example.data_jpa.controller;

import com.example.data_jpa.dto.MemberDto;
import com.example.data_jpa.entity.Member;
import com.example.data_jpa.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
     * ex: /members?page=0&size=3&sort=id,desc&sort=username,desc
     */
    @GetMapping("/members")
    public Page<Member> findMembers(Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        return page;
    }

    /**
     * 페이징과 정렬 - 개별 설정
     * - 글로벌 설정 또는 기본 설정이 아닌 개별 설정을 사용하려면 @PageableDefault 어노테이션을 사용한다.
     */
    @GetMapping("/members_individual")
    public Page<String> findMembersIndividual(@PageableDefault(size = 5, sort = "username", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        Page<String> map = page.map(e -> e.getUsername());
        return map;
    }

    /**
     * 페이징과 정렬 - DTO로 변환
     */
    @GetMapping("/members_to_dto")
    public Page<MemberDto> findMembersToDto(Pageable pageable) {
        return memberRepository.findAll(pageable).map(MemberDto::new);
    }

    @PostConstruct
    public void init() {
        for (int i = 1; i < 100; i++) {
            memberRepository.save(new Member("member" + i, i));
        }
    }
}
