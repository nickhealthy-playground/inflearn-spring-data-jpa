package com.example.data_jpa.repository;

import com.example.data_jpa.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}
