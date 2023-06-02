package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import static org.junit.jupiter.api.Assertions.*;

/**
 * springboot 2.2 이상부터 junit5 로 바뀐다
 *
 * //@RunWith(SpringRunner.class) juint5 올라오면서 생략 가능
 * @SpringBootTest // 스프링 빈 컨테이너 가져오기 위한 테스트
 *
 * @Rollback(value = false) 테스트에서 무조건 롤백 + flush 안하는데 해당 설정 false
 *
 * // 현재 == 비교, 동일 트랜젝션안의 영속성 컨텍스트의 인스턴스는 동일함을 보장한다 - 1차캐시
 * Assertions.assertThat(findMember).isEqualTo(member);
 * */


@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember(){
        Member member = new Member("회원");
        Member savedMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(savedMember.getId());

        org.assertj.core.api.Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(findMember.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member);
    }

}