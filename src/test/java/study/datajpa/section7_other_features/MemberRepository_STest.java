package study.datajpa.section7_other_features;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;
import study.datajpa.section1_4_repository.MemberRepository;
import study.datajpa.section1_4_repository.TeamRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepository_STest {

    //테스트는 생성자 인젝션이 더 좋다
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;


//    @Autowired
//    public MemberRepository7Test(MemberRepository7 memberRepository7, TeamRepository teamRepository) {
//        this.memberRepository7 = memberRepository7;
//        this.teamRepository = teamRepository;
//    }


    @Test
    public void specBasic(){
        Team teamA = new Team("teamA");

        em.persist(teamA);

        Member member1 = new Member("member1", 0 , teamA);
        Member member2 = new Member("member2", 0 , teamA);
        em.persist(member1);
        em.persist(member2);
        em.flush();
        em.clear();


        Specification<Member> spec = MemberSpec.username("member1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void queryByExample(){
        //given
        Team teamA = new Team("teamA");

        em.persist(teamA);

        Member m1 = new Member("m1", 0 , teamA);
        Member m2 = new Member("m2", 0 , teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        //기존
//        memberRepository6.findByNames("m1");

        //probe 생성
        Member member = new Member ("m1");
        Team team = new Team("teamA");
        member.changeTeam(team);

        //ExampleMatcher 생성,   age 프로퍼티는 무시
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("age");

        Example<Member> example = Example.of(member,matcher);
        List<Member> result = memberRepository.findAll(example);

        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("m1");

    }

    @Test
    public void projections(){
        Team teamA = new Team("teamA");

        em.persist(teamA);

        Member m1 = new Member("m1", 0 , teamA);
        Member m2 = new Member("m2", 0 , teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        //when
//        List<UsernameOnlyDto> result = memberRepository.findProjectionClassTypeByUsername("m1", UsernameOnlyDto.class);
//        for (UsernameOnlyDto usernameOnly : result) {
//            System.out.println("usernameOnly = " + usernameOnly);
//        }
        List<NestedClosedProjections> result = memberRepository.findProjectionClassTypeByUsername("m1", NestedClosedProjections.class);
        for (NestedClosedProjections nestedClosedProjections : result) {
            System.out.println("nestedClosedProjections = " + nestedClosedProjections);
        }

    }

    @Test
    public void nativeQuery(){
        Team teamA = new Team("teamA");

        em.persist(teamA);

        Member m1 = new Member("m1", 0 , teamA);
        Member m2 = new Member("m2", 0 , teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

//        Member result = memberRepository.findByNativeQuery("m1");
//        System.out.println("result = " + result);

        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));

        for (MemberProjection content : result) {
            System.out.println("content.getUsername() = " + content.getUsername());
            System.out.println("content.getTeamName() = " + content.getTeamName());
        }
    }
}