package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * springDataJPA 사용 test
 *
 * Optional<Member> - 값이 있을수도 있고 없을수도 있는 optional 사용
 *
 * //이렇게 가져오면 안된다~ optional 공부해서 잘 쓰자
 * Optional<Member> savedMember = memberRepository.findById(member.getId());
 * Member findMember = savedMember.get();
 * */

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {


    @Autowired MemberRepository memberRepository;

    @Test
    public void testMember(){
        Member member = new Member("회원1");
        memberRepository.save(member);

        Optional<Member> savedMember = memberRepository.findById(member.getId());

        Member findMember = savedMember.get();

        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member);

    }
}