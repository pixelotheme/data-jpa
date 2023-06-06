package study.datajpa.section1_4_repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * springDataJPA 사용 test
 * <p>
 * 설정 - DataJpaApplication 클래스
 * <p>
 * * JavaConfig 설정- 스프링 부트 사용시 생략 가능
 * *
 * * @Configuration
 * * @EnableJpaRepositories(basePackages = "jpabook.jpashop.repository")
 * * public class AppConfig {}
 * *
 * * 스프링 부트 사용시 @SpringBootApplication 어노테이션의 위치를 자동으로 지정(해당 패키지와 하위 패키지 인식)
 * * -> 만약 위치가 달라지면 @EnableJpaRepositories 필요
 * *
 * <p>
 * ===============
 * Optional<Member> - 값이 있을수도 있고 없을수도 있는 optional 사용
 * <p>
 * //이렇게 가져오면 안된다~ optional 공부해서 잘 쓰자
 * Optional<Member> savedMember = memberRepository.findById(member.getId());
 * Member findMember = savedMember.get();
 * <p>
 * =================
 * memberRepository에 injection 된 객체의 정체가 뭔가?
 * <p>
 * memberRepository.getClass() = class com.sun.proxy.$Proxy121
 * <p>
 * -> 구현체는 SpringDataJpa가 구현하여 Injection 해준다
 * -> 즉 우리는 인터페이스만 만들어 주면 된다
 * <p>
 * 정리
 * -> org.springframework.data.repository.Repository 를 구현한 클래스는 스캔 대상
 * MemberRepository 인터페이스가 동작한 이유
 * 실제 출력해보기(Proxy)
 * memberRepository.getClass() class com.sun.proxy.$ProxyXXX
 *
 * @Repository 애노테이션 생략 가능
 * 컴포넌트 스캔을 스프링 데이터 JPA가 자동으로 처리 -> 구현체는 SpringDataJpa가 구현하여 Injection 해준다
 * JPA 예외를 스프링 예외로 변환하는 과정도 자동으로 처리
 * <p>
 * =====================
 * <p>
 * <p>
 * 주의
 * T findOne(ID) ->  Optional<T> findById(ID) 변경
 * boolean exists(ID) -> boolean existsById(ID) 변경
 * <p>
 * 제네릭 타입
 * T : 엔티티
 * ID : 엔티티의 식별자 타입
 * S : 엔티티와 그 자식 타입
 * <p>
 * 주요 메서드
 * save(S) : 새로운 엔티티는 저장하고 이미 있는 엔티티는 병합한다.
 * delete(T) : 엔티티 하나를 삭제한다. 내부에서 EntityManager.remove() 호출
 * findById(ID) : 엔티티 하나를 조회한다. 내부에서 EntityManager.find() 호출
 * getOne(ID) : 엔티티를 프록시로 조회한다. 내부에서 EntityManager.getReference() 호출
 * findAll(…) : 모든 엔티티를 조회한다. 정렬( Sort )이나 페이징( Pageable ) 조건을 파라미터로 제공할 수 있다.
 * <p>
 * > 참고: JpaRepository 는 대부분의 공통 메서드를 제공한다
 * <p>
 * =========================
 * <p>
 * 만약 공통기능이 아닌 다른기능이 필요할때는????
 * <p>
 * -> 인터페이스 Impl 받아 구현하기 위해 다른 메서드들을 override 로 직접 모두 구현해야하는 문제
 * -> 인터페이스에서 바로 구현하면 된다
 * <p>
 * --> 쿼리 메소드 기능 이다
 */

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {


    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;

    @Test
    public void testSpringDataJpa() {
        System.out.println("memberRepository.getClass() = " + memberRepository.getClass());
    }

    @Test
    public void testMember() {
        Member member = new Member("회원1");
        memberRepository.save(member);

        Optional<Member> savedMember = memberRepository.findById(member.getId());

        Member findMember = savedMember.get();

        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member);

    }


    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        //optional 바로 get 하는것은 좋지않다 - 실제 사용시는 다르게 알아보자
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        Assertions.assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        //카운트 검증
        long deletedCount = memberRepository.count();
        Assertions.assertThat(deletedCount).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        Assertions.assertThat(result.get(0).getAge()).isEqualTo(20);
        Assertions.assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void testNamedQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        Assertions.assertThat(findMember).isEqualTo(member1);
    }

    @Test
    public void testQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        Member findMember = result.get(0);
        Assertions.assertThat(findMember).isEqualTo(member1);
    }

    @Test
    public void findUsernameList() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("팀이름");
        teamRepository.save(team);

        Member member1 = new Member("AAA", 10, team);
        Member member2 = new Member("BBB", 20, team);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Team team = new Team("팀이름");
        teamRepository.save(team);

        Member member1 = new Member("AAA", 10, team);
        Member member2 = new Member("BBB", 20, team);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member dto : members) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void returnType() {
        Team team = new Team("팀이름");
        teamRepository.save(team);

        Member member1 = new Member("AAA", 10, team);
        Member member2 = new Member("AAA", 20, team);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> aaa = memberRepository.findListByUsername("AAA");
        for (Member member : aaa) {
            System.out.println("member = " + member);
        }

        Member aaa1 = memberRepository.findMemberByUsername("AAA");
        System.out.println("aaa1 = " + aaa1);

        Optional<Member> aaa2 = memberRepository.findOptionalByUsername("AAA");
        System.out.println("aaa2.get() = " + aaa2.get());
    }

    @Test
    public void paging() {
        //given
        memberRepository.save(new Member("AAA1", 10));
        memberRepository.save(new Member("AAA2", 10));
        memberRepository.save(new Member("AAA3", 10));
        memberRepository.save(new Member("AAA4", 10));
        memberRepository.save(new Member("AAA5", 10));
        memberRepository.save(new Member("AAA6", 10));


        int age = 10;
        //springDataJPA 는 페이징이 0 부터 시작 - 1부터가 아니다
        //-> 0페이지부터 3개 가져와 / sorting 조건 "username" DESC 로 한다
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        // 반환타입이 Page 이면 totalcount 쿼리까지 같이 날라간다다
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        //map을 통해 Page 안의 Member객체에 접근하여 데이터를 뽑고 Page 로 반환받을수 있다
        //api 반환시 엔티티를 반환하면 안된다
        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        //then
        //실제 데이터
        List<Member> content = page.getContent();
        // totalcount
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        Assertions.assertThat(content.size()).isEqualTo(3);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(6);
        // 페이지 번호
        Assertions.assertThat(page.getNumber()).isEqualTo(0);
        //총 페이지 개수
        Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
        //첫 페이지인지 ?
        Assertions.assertThat(page.isFirst()).isEqualTo(true);
        //다음 페이지가 있는지?
        Assertions.assertThat(page.hasNext()).isEqualTo(true);

    }

    @Test
    public void slicePaging() {
        //given
        memberRepository.save(new Member("AAA1", 10));
        memberRepository.save(new Member("AAA2", 10));
        memberRepository.save(new Member("AAA3", 10));
        memberRepository.save(new Member("AAA4", 10));
        memberRepository.save(new Member("AAA5", 10));
        memberRepository.save(new Member("AAA6", 10));


        int age = 10;
        //springDataJPA 는 페이징이 0 부터 시작 - 1부터가 아니다
        //-> 0페이지부터 3개 가져와 / sorting 조건 "username" DESC 로 한다
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        //slice 는 3개를 요청하면 limit 에 +1 해서 4개를 가져온다 - 전체를 가져오지 않는다 - totalcount 쿼리 없이 사용한다
        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);

        //then
        //실제 데이터
        List<Member> content = page.getContent();

        for (Member member : content) {
            System.out.println("member = " + member);
        }

        Assertions.assertThat(content.size()).isEqualTo(3);
        // 페이지 번호
        Assertions.assertThat(page.getNumber()).isEqualTo(0);
        //첫 페이지인지 ?
        Assertions.assertThat(page.isFirst()).isEqualTo(true);
        //다음 페이지가 있는지?
        Assertions.assertThat(page.hasNext()).isEqualTo(true);

    }
}