package com.example.data_jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 엔티티를 생성, 변경할 때 변경한 사람과 시간을 추적하고 싶을 때 사용(스프링 데이터 JPA)
 * 사용 어노테이션
 * - @CreatedDate
 * - @LastModifiedDate
 * - @CreatedBy
 * - @LastModifiedBy
 */
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity extends BaseTimeEntity {

    @CreatedBy
    @Column(updatable = false)
    private String createdBy; // 등록자

    @LastModifiedBy
    private String lastModifiedBy; // 수정자
}
