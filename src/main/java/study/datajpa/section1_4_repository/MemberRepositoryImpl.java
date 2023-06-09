package study.datajpa.section1_4_repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * 확장 기능
 *
 * * 사용자 정의 리포지토리 구현 *
 *
 * 스프링 데이터 JPA 리포지토리는 인터페이스만 정의하고 구현체는 스프링이 자동 생성
 *
 * 스프링 데이터 JPA가 제공하는 인터페이스를 직접 구현하면 구현해야 하는 기능이 너무 많음
 * -> data JPA 를 사용하지 않고 ~ custom 화 하려면 ~ 순수 jpa 쓰듯이 해야하니 귀찮다는 뜻
 *
 * 다양한 이유로 인터페이스의 메서드를 직접 구현하고 싶다면?
 * 1. JPA 직접 사용( EntityManager )
 * 2. 스프링 JDBC Template 사용
 * 3. MyBatis 사용
 * 4. 데이터베이스 커넥션 직접 사용 등등...
 * 5 .Querydsl 사용
 *
 *  custom 인터페이스 패키지가 다를시 에러가 난다,,,
 *  1. MemberRepositoryCustom - Interface 생성
 *  2. MemberRepositoryImpl - 구현 클래스 생성(MemberRepositoryCustom)
 *  3. MemberRepository - DataJPA에 인터페이스 상속 (인터페이스라 다중상속 가능)
 *
 *  -> JAVA 기능이 아니라 DataJpa가 지원하는 기능이다
 *
 *-------------------
 * 구현체와 인터페이스 명명 규칙
 * SpringDataJpa 인터페이스 명칭 + Impl
 *
 * 스프링 데이터 JPA가 인식해서 스프링 빈으로 등록
 *
 * -> 관례를 가능하면 따르는데 xml, JavaConfig 설정도 있다
 *
 *  XML 설정
 * <repositories base-package="study.datajpa.repository"
 *  repository-impl-postfix="Impl" />
 *
 *
 * JavaConfig 설정
 *
 * @EnableJpaRepositories(basePackages = "study.datajpa.repository",
 *  repositoryImplementationPostfix = "Impl")
 *
 *  ----------------------------------
 *
 * > 참고: 실무에서는 주로 QueryDSL이나 SpringJdbcTemplate을 함께 사용할 때 사용자 정의 리포지토
 * 리 기능 자주 사용
 *
 *
 * --> 복잡도가 올라가 구분하기가 힘들다...
 *
 * -> 참고: 항상 사용자 정의 리포지토리가 필요한 것은 아니다.
 * 그냥 임의의 리포지토리를 만들어도 된다.
 * 예를 들어 MemberQueryRepository를 인터페이스가 아닌 클래스로 만들고 스프링 빈으로 등록해서
 * 그냥 직접 사용해도 된다.
 * 물론 이 경우 스프링 데이터 JPA와는 아무런 관계 없이 별도로 동작한다.
 *
 * -> 모든것을 한가지 도구로 통합하려고만 하지말자 - 망하는 지름길
 * -> 실무도 비즈니스적 로직은 그 자체만으로 이해하기 복잡하고, 수정 라이프사이클에서도 화면이 수정되면 같이 수정된다
 * ->복잡도를 낮추기위해 별도의  MemberQueryRepository_5 Repository를 만들어 줬다
 *
 *  *** 사용자 정의 리포지토리 구현 최신 방식 ***
 * (참고: 강의 영상에는 없는 내용입니다.)
 * 스프링 데이터 2.x 부터는 사용자 정의 구현 클래스에 리포지토리 인터페이스 이름 + Impl 을 적용하는 대
 * 신에
 * 사용자 정의 인터페이스 명 + Impl 방식도 지원한다.
 * 예를 들어서 위 예제의 MemberRepositoryImpl 대신에 MemberRepositoryCustomImpl 같이 구현해도
 * 된다.
 *
 * 기존 방식보다 이 방식이 사용자 정의 인터페이스 이름과 구현 클래스 이름이 비슷하므로 더 직관적이다. 추
 * 가로 여러 인터페이스를 분리해서 구현하는 것도 가능하기 때문에 새롭게 변경된 이 방식을 사용하는 것을
 * 더 권장한다.
 *
 *
 * */

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{


    private final EntityManager em;

    //이런식으로 jpa 도 사용, JDBC 커넥션 - connection 얻어서 사용 ...
    //Impl 구현체에서 사용하면된다
    @Override
    public List<Member> findMemberCustom(){
        return em.createQuery("select m from Member m ")
                .getResultList();
    }

}
