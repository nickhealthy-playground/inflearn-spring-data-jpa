package com.example.data_jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 엔티티를 생성, 변경할 때 변경한 사람과 시간을 추적하고 싶을 때 사용(순수 JPA 사용)
 * 사용 어노테이션
 * - @PrePersist, @PostPersist
 * - @PreUpdate, @PostUpdate
 */
@MappedSuperclass
@Getter
public class JpaBaseEntity {

    @Column(updatable = false)
    private LocalDateTime createdDate; // 등록일
    private LocalDateTime updatedDate; // 수정일

    @PrePersist // 저장하기 전 동작, @PostPersist도 존재
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    @PreUpdate // Update 하기 전 동작, @PostUpdate도 존재
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
