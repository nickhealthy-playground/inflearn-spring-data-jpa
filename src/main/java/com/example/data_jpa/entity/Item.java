package com.example.data_jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 스프링 데이터 JPA - save() 동작원리
 *  - 새로운 엔티티면 persist(), 기존 엔티티면 merge() 호출
 *  - 새로운 엔티티는 persist() 시 식별자가 객체일 땐 null, 식별자가 기본값 타입일 땐 0으로 판단
 *  - merge() 호출 시 select 쿼리가 추가로 발생하므로 성능 저하 및 모든 엔티티의 필드 데이터를 갈아끼우기 때문에 조심해야함.
 * 1. 새로운 엔티티 구별방법
 *  - Persistable 인터페이스의 isNew() 오버라이딩해서 판단 로직 변경 가능
 *  - 주로 @CreateDate 어노테이션을 이용해 엔티티가 날짜를 생성하지 않았으면 새로운 엔티티로 판단, 날자를 생성했으면 기존 객체로 판단하는 전략을 자주 사용함
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<Long> {

    @Id
    private Long id;

    @CreatedDate
    private LocalDateTime createdDate;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}
