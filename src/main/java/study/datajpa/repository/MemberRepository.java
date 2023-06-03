package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

/**
 * 공통된 기능이 아닌 기능 사용방법
 * */

public interface MemberRepository extends JpaRepository<Member, Long> {

}
