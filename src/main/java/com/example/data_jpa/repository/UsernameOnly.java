package com.example.data_jpa.repository;

import com.example.data_jpa.entity.Team;
import org.springframework.beans.factory.annotation.Value;

/**
 * 인터페이스 기반 Projections
 * - 프로퍼티 형식(getter)의 인터페이스를 제공하면, 구현체는 스프링 데이터 JPA가 제공
 */
public interface UsernameOnly {
    /*Close Projections*/
    // 인터페이스에 정의한 목록만 DTO처럼 가져올 수 있음
//    String getUsername();
//    Team getTeam();
//    String getTeamName();

    /*Open Projections*/
    // 전체 엔티티 필드를 모두 가져옴 -> 이후에 SPEL로 정의된 값만 재조립
    @Value("#{target.username + ' ' + target.age + ' ' + target.team.name}")
    String getUsername();
}
