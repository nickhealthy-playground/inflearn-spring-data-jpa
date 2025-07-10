package com.example.data_jpa.repository;

import com.example.data_jpa.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 사용자 정의 리포지토리 구현
 * 문제: 스프링 데이터 JPA가 제공하는 인터페이스를 직접 구현하면 구현해야 할 기능이 너무 많음. -> 따라서 필요한 것만 따로 뗴어내서 분리할 필요성이 있음
 * 구현 방법:
 * - 1. 사용자 정의 인터페이스를 생성 후 메서드 정의
 * - 2. 인터페이스를 구현한 클래스 생성, 이때 클래스 이름은 스프링 데이터 JPA 인터페이스를 상속받은 '인터페이스명 + Impl'로 구현해야 함
 * - 3. 1번에서 생성한 인터페이스를 스프링 데이터 JPA 인터페이스를 상속받은 인터페이스에 추가로 상속
 */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }
}
