package study.datajpa.section1_4_repository;


import study.datajpa.entity.Member;

import java.util.List;

/**
 * 확장 기능
 *
 * */
public interface MemberRepositoryCustom {

    //구현할 메서드
    List<Member> findMemberCustom();
}
