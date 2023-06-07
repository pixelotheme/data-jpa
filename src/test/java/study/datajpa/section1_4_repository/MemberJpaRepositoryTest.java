package study.datajpa.section1_4_repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

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

    @Test
    @DisplayName("변경감지 테스트")
    public void updateQuery(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        //단건 조회 검증
        //optional 바로 get 하는것은 좋지않다 - 실제 사용시는 다르게 알아보자
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);

        findMember1.changeUserName("member1수정");
        findMember2.changeUserName("member2수정");
    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        //단건 조회 검증
        //optional 바로 get 하는것은 좋지않다 - 실제 사용시는 다르게 알아보자
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberJpaRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberJpaRepository.count();
        Assertions.assertThat(count).isEqualTo(2);

        //삭제 검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        //카운트 검증
        long deletedCount = memberJpaRepository.count();
        Assertions.assertThat(deletedCount).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThen(){
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        List<Member> result = memberJpaRepository.findByUsernameAndAgeGreaterThen("AAA", 15);

        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        Assertions.assertThat(result.get(0).getAge()).isEqualTo(20);
        Assertions.assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void testNamedQuery(){
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        List<Member> result = memberJpaRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        Assertions.assertThat(findMember).isEqualTo(member1);
    }

    @Test
    public void paging(){
        //given
        memberJpaRepository.save(new Member("AAA1", 10));
        memberJpaRepository.save(new Member("AAA2", 10));
        memberJpaRepository.save(new Member("AAA3", 10));
        memberJpaRepository.save(new Member("AAA4", 10));
        memberJpaRepository.save(new Member("AAA5", 10));
        memberJpaRepository.save(new Member("AAA6", 10));

        int age = 10;
        int offset = 1;
        int limit = 3;
        //when
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long total = memberJpaRepository.totalCount(age);

        //Data JPA 에 지원하는 내용들이 있다 ~
        //페이지 계산 공식 적용
        //totalPage = totalCount / size...
        //마지막 페이지..
        // 최초 페이지

        //then
        Assertions.assertThat(members.size()).isEqualTo(3);
        Assertions.assertThat(total).isEqualTo(6);

    }
    @Test
    public void bulkUpdate(){
        memberJpaRepository.save(new Member("AAA1", 10));
        memberJpaRepository.save(new Member("AAA2", 19));
        memberJpaRepository.save(new Member("AAA3", 20));
        memberJpaRepository.save(new Member("AAA4", 21));
        memberJpaRepository.save(new Member("AAA5", 30));
        memberJpaRepository.save(new Member("AAA6", 40));

        int resultCount = memberJpaRepository.bulkAgePlus(20);

        //then
        Assertions.assertThat(resultCount).isEqualTo(4);

    }
}