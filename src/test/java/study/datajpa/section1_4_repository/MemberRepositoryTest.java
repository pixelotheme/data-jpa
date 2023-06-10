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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    //테스트는 생성자 인젝션이 더 좋다
//    @Autowired
    MemberRepository memberRepository;
//    @Autowired
    TeamRepository teamRepository;

    MemberQueryRepository_5 memberQueryRepository5;
    @PersistenceContext
    EntityManager em;

    @Autowired
    public MemberRepositoryTest(MemberRepository memberRepository, TeamRepository teamRepository, MemberQueryRepository_5 memberQueryRepository5) {
        this.memberRepository = memberRepository;
        this.teamRepository = teamRepository;
        this.memberQueryRepository5 = memberQueryRepository5;
    }

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

    @Test
    public void bulkUpdate() {
        memberRepository.save(new Member("AAA1", 10));
        memberRepository.save(new Member("AAA2", 19));
        memberRepository.save(new Member("AAA3", 20));
        memberRepository.save(new Member("AAA4", 21));
        memberRepository.save(new Member("AAA5", 30));
        memberRepository.save(new Member("AAA6", 40));

        // update 쿼리가 나가기 전에 JPA 쿼리 가 먼저 나간후
        // JPQL 쿼리가 나간다 - flush 따로 안해줘도 된다
        int resultCount = memberRepository.bulkAgePlus(20);

        //@Modifying(clearAutomatically = true)  - 적용
//        em.clear();

        //flush 없이 호출하면 40 이 나온다 - 1차 캐시에 있는 데이터가 나옴
        List<Member> aaa6 = memberRepository.findByUsername("AAA6");
        Member member = aaa6.get(0);
        System.out.println("member = " + member);

        //then
        Assertions.assertThat(resultCount).isEqualTo(4);

    }

    @Test
    public void findMemberLazy() {
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.Team = " + member.getTeam().getName());
        }
    }

    @Test
    public void findMemberLazyFetchJoin() {
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findMemberFetchJoin();
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.Team = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        //변경감지를 위해 기존 member 와 변경된 member를 비교하는 과정이 필요
        // 스냅샷을 통해 .... 결국 2개의 객체를 관리하고 있는것
//        Member findMember = memberRepository.findById(member1.getId()).get();
//        findMember.changeUserName("member2");
        //100% 조회용으로만 쓸경우 최적화 - hibernate는 제공하는데 jpa 는 제공 안함 그래서 hint를 줄수 있게 함

        Member findMember = memberRepository.findReadOnlyByUsername(member1.getUsername());
        findMember.changeUserName("member2");
        em.flush();
    }

    @Test
    public void Lock(){
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        List<Member> lockByUsername = memberRepository.findLockByUsername(member1.getUsername());
    }


    //순수 jpa entity MappedSuperclass 사용
    @Test
    public void JpaEventBaseEntity() throws Exception{
        //given
        Member member = new Member("member1");
        memberRepository.save(member); // @PrePersist 발생

        Thread.sleep(100);
        member.changeUserName("member11");
        //when
        em.flush(); // @PreUpdate 발생
        em.clear();

        Member member1 = memberRepository.findById(member.getId()).get();
        System.out.println("member1 = " + member1.getCreatedDate());
        System.out.println("member1 = " + member1.getLastModifiedDate());
//        System.out.println("member1 = " + member1.getCreatedBy());
//        System.out.println("member1 = " + member1.getLastModifiedBy());
        //then

    }
}