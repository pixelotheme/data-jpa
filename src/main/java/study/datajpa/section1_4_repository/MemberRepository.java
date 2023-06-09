package study.datajpa.section1_4_repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.section7_other_features.MemberProjection;
import study.datajpa.section7_other_features.NestedClosedProjections;
import study.datajpa.section7_other_features.UsernameOnly;
import study.datajpa.section7_other_features.UsernameOnlyDto;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * springDataJPA 사용
 *
 * 목차
 * 1. 메소드 이름으로 쿼리생성
 * 2. JPA NamedQuery
 * 3. @Query, 리포지토리에 메소드에 쿼리 정의
 * 4. Query, 값, DTO 조회
 * 5. 반환 타입
 * 6. JPA 페이징, 정렬
 * 7. 벌크성 수정 쿼리
 * 8. @EntityGraph
 * ============================================
 *
 * 설정 - DataJpaApplication 클래스
 *
 *  * JavaConfig 설정- 스프링 부트 사용시 생략 가능
 *  *
 *  * @Configuration
 *  * @EnableJpaRepositories(basePackages = "jpabook.jpashop.repository")
 *  * public class AppConfig {}
 *  *
 *  * 스프링 부트 사용시 @SpringBootApplication 어노테이션의 위치를 자동으로 지정(해당 패키지와 하위 패키지 인식)
 *  * -> 만약 위치가 달라지면 @EnableJpaRepositories 필요
 *  *
 *
 * ===============
 * Optional<Member> - 값이 있을수도 있고 없을수도 있는 optional 사용
 *
 * //이렇게 가져오면 안된다~ optional 공부해서 잘 쓰자
 * Optional<Member> savedMember = memberRepository.findById(member.getId());
 * Member findMember = savedMember.get();
 *
 * =================
 * memberRepository에 injection 된 객체의 정체가 뭔가?
 *
 * memberRepository.getClass() = class com.sun.proxy.$Proxy121
 *
 * -> 구현체는 SpringDataJpa가 구현하여 Injection 해준다
 * -> 즉 우리는 인터페이스만 만들어 주면 된다
 *
 * 정리
 * -> org.springframework.data.repository.Repository 를 구현한 클래스는 스캔 대상
 *  MemberRepository 인터페이스가 동작한 이유
 *  실제 출력해보기(Proxy)
 *  memberRepository.getClass() class com.sun.proxy.$ProxyXXX
 *
 * @Repository 애노테이션 생략 가능
 *  컴포넌트 스캔을 스프링 데이터 JPA가 자동으로 처리 -> 구현체는 SpringDataJpa가 구현하여 Injection 해준다
 *  JPA 예외를 스프링 예외로 변환하는 과정도 자동으로 처리
 *
 * =====================
 *
 *
 *  주의
 *  T findOne(ID) ->  Optional<T> findById(ID) 변경
 *  boolean exists(ID) -> boolean existsById(ID) 변경
 *
 *  제네릭 타입
 *      T : 엔티티
 *      ID : 엔티티의 식별자 타입
 *      S : 엔티티와 그 자식 타입
 *
 *  주요 메서드
 *  save(S) : 새로운 엔티티는 저장하고 이미 있는 엔티티는 병합한다.
 *  delete(T) : 엔티티 하나를 삭제한다. 내부에서 EntityManager.remove() 호출
 *  findById(ID) : 엔티티 하나를 조회한다. 내부에서 EntityManager.find() 호출
 *  getOne(ID) : 엔티티를 프록시로 조회한다. 내부에서 EntityManager.getReference() 호출
 *  findAll(...) : 모든 엔티티를 조회한다. 정렬( Sort )이나 페이징( Pageable ) 조건을 파라미터로 제공할 수 있다.
 *
 * > 참고: JpaRepository 는 대부분의 공통 메서드를 제공한다
 *
 * =========================
 *
 * 쿼리 메소드 기능 3가지
 *  메소드 이름으로 쿼리 생성
 *  메소드 이름으로 JPA NamedQuery 호출 - 실무에서 거의 쓰지 않음
 *  @Query 어노테이션을 사용해서 리파지토리 인터페이스에 쿼리 직접 정의
 *
 *  만약 공통기능이 아닌 다른기능이 필요할때는????
 *
 *  -> 인터페이스 Impl 받아 구현하기 위해 다른 메서드들을 override 로 직접 모두 구현해야하는 문제
 *  -> 인터페이스에서 바로 구현하면 된다
 *
 *  --> 쿼리 메소드 기능 이다
 *  메서드 이름으로 쿼리를 생성한다
 *
 * 스프링 데이터 JPA는 메소드 이름을 분석해서 JPQL을 생성하고 실행
 *
 * 쿼리 메소드 필터 조건
 * 스프링 데이터 JPA 공식 문서 참고:
 * (https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation)
 *
 * =============================================
 * ---스프링 데이터 JPA가 제공하는 쿼리 메소드 기능
 *
 *
 * 조회: find...By ,read...By ,query...By get...By,
 *  (https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation)
 *
 *  예:) findHelloBy 처럼 ...에 식별하기 위한 내용(설명)이 들어가도 된다.
 * ** ... 생략가능, 아무거나 들어가도 된다 find맘대로By~~
 *
 * -COUNT: count...By 반환타입 long
 * -EXISTS: exists...By 반환타입 boolean
 * -삭제: delete...By, remove...By 반환타입 long
 * -DISTINCT: findDistinct, findMemberDistinctBy
 * -LIMIT: findFirst3, findFirst, findTop, findTop3
 *
 *  https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.limit-query-result
 *
 * > 참고: 이 기능은 엔티티의 필드명이 변경되면 인터페이스에 정의한 메서드 이름도 꼭 함께 변경해야 한다.
 * 그렇지 않으면 애플리케이션을 시작하는 시점에 오류가 발생한다.
 * > 이렇게 애플리케이션 로딩 시점에 오류를 인지할 수 있는 것이 스프링 데이터 JPA의 매우 큰 장점이다.
 *
 * ==================
 *  문제점 ....
 *  조건이 많을수록 메소드명이 너무 길어진다
 *
 * -> 쿼리가 복잡할때 처리방법
 *
 *  JPA NamedQuery - 거의 쓰지 않음
 *
 *  1. 엔티티에 @NamedQuery 정의
 *  2. Repository에서 사용 -
 *
 *  1. 엔티티정의
 *  @NamedQuery(
 *         name = "Member.findByUsername",
 *         query = "select m from Member m where m.username = :username "
 *          )
 *
 *  2-1 Repository 사용 - 순수 jpa
 *
 *      //NamedQuery 테스트
 *     public List<Member> findByUsername(String username){
 *         return em.createNamedQuery("Member.findByUsername", Member.class)
 *                 .setParameter("username", username)
 *                 .getResultList();
 *     }
 *
 *  2-2 Repository 사용 - Data JPA
 *
 *     //JPQL에 파라미터가 있을때는 NamedParam이 필요해서 @Param을 사용한다
 *     @Query(name = "Member.findByUsername") -- 메서드명이 같을경우 @Query 생략가능
 *     List<Member> findByAmuguna(@Param("username") String username);
 *
 * -----------
 * 장점
 * 어플리케이션 로딩시점에 쿼리 오류를 내준다 - 기존 JPQL은 문법오류가 나도 test 성공했었다
 *
 * 단점
 * 너무 번잡스럽다, 엔티티에 직접 넣어주는 상황
 * - 물론 바꿀수 있지만 더좋은 기능을 사용해보자
 *
 * =================================
 *
 * @Query, 리포지토리 메소드에 쿼리 정의하기 - 많이 쓴다
 *
 * ** JPQL에 파라미터가 있을때는 NamedParam이 필요해서 @Param을 사용한다
 *
 *     @Query("select m from Member m where m.username = :username and m.age = :age")
 *     List<Member> findUser(@Param("username") String username, @Param("age") int age);
 *
 * 애플리케이션 실행시점에 쿼리 오류를 잡을수 있다
 *
 * -----
 *
 * @Query, 값, DTO 조회하기
 *
 * //  @Query, 값,
 *     @Query("select m.username from Member m")
 *     List<String> findUsernameList();
 *
 *     //  DTO 조회하기 - 마치 생성자로 new 하는것처럼 매칭해서 적어줘야한다
 *     @Query("select new study.datajpa.dto.MemberDto( m.id, m.username, t.name ) from Member m join m.team t")
 *     List<MemberDto> findMemberDto();
 *
 * =======================
 *
 * 파라미터 바인딩 - 이름기반으로 사용하자자 *
 * 위치 기반
 * 이름 기반 - 이름기반으로만 사용하자 가독성, 유지보수성
 *
 * @Query("select m from Member m where m.username = :name")
 *  Member findMembers(@Param("name") String username);
 *
 * --
 *
 * 컬렉션 파라미터 바인딩 ******************** 자주 사용한다
 * Collection 타입으로 in절 지원
 * @Query("select m from Member m where m.username in :names")
 * List<Member> findByNames(@Param("names") Collection<String> names);
 *
 * -> 상위 타입인 컬렉션으로 사용했다 - List,set .... 모두 사용 가능
 *
 *
 * =================================================
 *
 * 반환 타입
 *
 * 반환 값이 없을때 빈 컬렉션을 반환해준다 - size = 0
 * List<Member> findByUsername(String name); //컬렉션
 * -> 무조건 null 이 아니다
 *
 * Member findByUsername(String name); //단건 - 순수 jpa 에서는 예외 터트린다 -> dataJPA가 예외처리
 * -> 없으면 null
 *
 * Optional<Member> findByUsername(String name); //단건 Optional - 1.8에서 optional로 null 처리
 * -> 값이 다건 이면 org.springframework.dao.IncorrectResultSizeDataAccessException: 에러 발생
 * -> 각각의 odbc, jdbc, jpa 등 으로 repository가 의존중일때 spring에서 예외를 변환해서 동일하게 반환해준다
 *
 *
 * 조회 결과가 많거나 없으면?
 *
 * 컬렉션
 *  결과 없음: 빈 컬렉션 반환
 *
 * 단건 조회
 *  결과 없음: null 반환
 *  결과가 2건 이상: javax.persistence.NonUniqueResultException 예외 발생
 *
 * > 참고: 단건으로 지정한 메서드를 호출하면 스프링 데이터 JPA는 내부에서 JPQL의
 * Query.getSingleResult() 메서드를 호출한다. 이 메서드를 호출했을 때 조회 결과가 없으면
 * javax.persistence.NoResultException 예외가 발생하는데 개발자 입장에서 다루기가 상당히 불편하
 * 다. 스프링 데이터 JPA는 단건을 조회할 때 이 예외가 발생하면 예외를 무시하고 대신에 null 을 반환한다.
 *
 * 반환 타입 종류
 * 스프링 데이터 JPA 공식 문서: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repository-query-return-types
 *
 *
 *
 * =================================
 *
 * 페이징 처리
 *
 * 스프링 데이터 JPA 페이징과 정렬
 *
 * 페이징과 정렬 파라미터 - jpa.data 패키지가 아닌 완전 상위로 공통화 해버렸다
 *  org.springframework.data.domain.Sort : 정렬 기능
 *  org.springframework.data.domain.Pageable : 페이징 기능 (내부에 Sort 포함)
 *
 * 특별한 반환 타입
 *
 *  - org.springframework.data.domain.Page : 추가 count 쿼리 결과를 포함하는 페이징
 *  -> totalcount 필요한 페이징
 *
 *  - org.springframework.data.domain.Slice : 추가 count 쿼리 없이 다음 페이지만 확인 가능(내부적으로 limit + 1조회)
 *  -> 모바일 에서 10개씩 나오고 더보기 버튼 누르면 10개 더나올때 사용
 *  -> 눈속임으로 11개 가져온뒤 11번째가 있으면 더보기 버튼 생김, 없으면 마지막페이지
 *
 *  - List (자바 컬렉션): 추가 count 쿼리 없이 결과만 반환
 *
 *
 * ========
 * Pagealbe 파라미터의 구현체 사용법
 *
 * 두 번째 파라미터로 받은 Pageable 은 인터페이스다. 따라서 실제 사용할 때는
 * 해당 인터페이스를 구현한 org.springframework.data.domain.PageRequest 객체를 사용한다.
 * PageRequest 생성자의 첫 번째 파라미터에는 현재 페이지를, 두 번째 파라미터에는 조회할 데이터 수를
 * 입력한다. 여기에 추가로 정렬 정보도 파라미터로 사용할 수 있다. 참고로 페이지는 0부터 시작한다.
 *
 * > 주의: Page는 1부터 시작이 아니라 0부터 시작이다.
 *
 * ========================
 * 사용 방법
 * -- 테스트 클래스 --
 * Page 타입 리턴일때 - PageRequest 사용
 * Page<Member> findByAge(int age, Pageable pageable);
 *
 *         int age = 10;
 *         //springDataJPA 는 페이징이 0 부터 시작 - 1부터가 아니다
 *         //-> 0페이지부터 3개 가져와 / sorting 조건 "username" DESC 로 한다
 *         PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
 *
 *         //when
 *         // 반환타입이 Page 이면 totalcount 쿼리까지 같이 날라간다다
 *         Page<Member> page = memberRepository.findByAge(age, pageRequest);
 *
 *         //then
 *         //실제 데이터
 *         List<Member> content = page.getContent();
 *         // totalcount
 *         long totalElements = page.getTotalElements();
 *
 *
 *         ----------------------------
 *
 * //slice 는 3개를 요청하면 limit 에 +1 해서 4개를 가져온다 - 전체를 가져오지 않는다 - totalcount 쿼리 없이 사용한다
 * Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);
 *
 * -----------------------------
 *
 *     // 반환타입이 Page 이면 totalcount 쿼리까지 같이 날라간다다
 *     //countQuery 를 조인없이 성능을 최적화 할수 있다
 *     @Query(value = "select m from Member m left join m.team t",
 *             countQuery = "select count(m.username) from Member m")
 *     Page<Member> findByAge(int age, Pageable pageable);
 *
 * ------------
 * //map을 통해 Page 안의 Member객체에 접근하여 데이터를 뽑고 Page 로 반환받을수 있다
 * //api 반환시 엔티티를 반환하면 안된다
 * Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
 *
 *
 * =========================
 *
 * 벌크성 수정 쿼리
 *
 * 보통 엔티티를 하나 가져와 변경감지로 트랜잭션 커밋시점에 쿼리가 나간다
 * -> 단건씩 update 친다
 *
 * -> 만약 모든 직원 10% 월급 인상
 * -> 한번에 update 칯는것이 좋다
 * -> 벌크성 쿼리 - JPA 엔티티 객체가 중심이라 분리되어 있다
 *
 * -------------
 * 순수 JPA
 *         int resultCnt = em.createQuery("update Member m set m.age = m.age + 1 where m.age >= :age")
 *                 .setParameter("age", age)
 *                 .executeUpdate();
 *
 * Data JPA
 *
 *    @Modifying // - 순수 JPA 의 executeUpdate 와 같다
 *     @Query(value = "update Member m set m.age = m.age + 1 where m.age >= :age")
 *     int bulkAgePlus(@Param("age") int age);
 *
 *
 * ----- 주의점 ----
 * 벌크성 수정, 삭제 쿼리는 @Modifying 어노테이션을 사용
 *  사용하지 않으면 다음 예외 발생
 *  org.hibernate.hql.internal.QueryExecutionRequestException: Not supported for DML operations
 *
 * 벌크성 쿼리를 실행하고 나서 영속성 컨텍스트 초기화: @Modifying(clearAutomatically = true)
 * (이 옵션의 기본값은 false )
 *    이 옵션 없이 회원을 findById 로 다시 조회하면 영속성 컨텍스트에 과거 값이 남아서 문제가 될 수
 *    있다. 만약 다시 조회해야 하면 꼭 영속성 컨텍스트를 초기화 하자.
 *
 * > 참고: 벌크 연산은 영속성 컨텍스트를 무시하고 실행하기 때문에, 영속성 컨텍스트에 있는 엔티티의 상태와
 * DB에 엔티티 상태가 달라질 수 있다.
 * -> em.persist 로 영속화 시킨 엔티티를 트랜잭션 커밋 이전에 update를 날린다
 *
 * ** 참고
 * > 1. 영속성 컨텍스트에 엔티티가 없는 상태에서 벌크 연산을 먼저 실행한다.
 * > 2. 부득이하게 영속성 컨텍스트에 엔티티가 있으면 벌크 연산 직후 영속성 컨텍스트를 초기화 한다.
 *
 * 1. 방법
 *  update 쿼리가 나가기 전에 JPA 쿼리 가 먼저 나간후
 *  JPQL 쿼리가 나간다 - flush 따로 안해줘도 된다
 * int resultCount = memberRepository.bulkAgePlus(20);
 *
 * em.clear();
 *
 * 2. 방법
 * @Modifying(clearAutomatically = true)  - clear 자동으로 1차 캐시 정리
 *
 * ---> 그러나 JDBC Templete, Mybatis 등을 사용 하여
 * 직접 쿼리를 날릴경우 JPA 가 인식하지 못해 영속성 컨텍스트와 오차가 발생
 * -> flush, clear 해줘야 한다
 *
 * =================================
 * @EntityGraph - 페치 조인 의 간편 버전
 * -> JPA 에서 지원하는 기능
 *
 * - 페치 조인이 필요한 이유
 * -> Lazy 로딩으로 team 프록시를 꺼내왔을때
 * member1 -> teamA
 * member2 -> teamB
 *
 * -> 전체 회원 조회 1번
 * -> 팀이 총 2개 - 쿼리 2번
 *
 * -> 쿼리 총 3번 (N + 1)
 *
 * -- 사용법
 *     @EntityGraph(attributePaths = {"team"})
 *     @Query("select m from Member m")
 *     List<Member> findMemberEntityGraph();
 *
 *--> 엔티티에서 직접 적용할수도 있다
 *
 * -> 정리
 * 간단할때는 DataJpa 에서 @EntityGraph 사용
 * -> 복잡할때는 JPQL fetch Join을 이용
 *
 *==========================
 * JPA Hint & Lock
 *
 * JPA 쿼리 힌트(SQL 힌트가 아니라 JPA 구현체에게 제공하는 힌트)
 * -> 지금은 hibernate 에 알려주는것
 * -> jpa 표준은 인터페이스의 모음
 *
 * 변경감지를 위해 기존 member 와 변경된 member를 비교하는 과정이 필요
 *  스냅샷을 통해 .... 결국 2개의 객체를 관리하고 있는것
 *  findMember = memberRepository.findById(member1.getId()).get();
 * findMember.changeUserName("member2");
 * 100% 조회용으로만 쓸경우 최적화 - hibernate는 제공하는데 jpa 는 제공 안함 그래서 hint를 줄수 있게 함
 *
 *
 * JPA Hint & LocK
 * 100% 조회용으로만 쓸경우 최적화 - hibernate는 제공하는데 jpa 는 제공 안함 그래서 hint를 줄수 있게 함
 * 아무거나 hibernate 에 넘길수 있게 열어둔 것
 * readOnly - true 가 되어있으면 스냅샷 안만들어 버린다 - 최적화 되어 변경 안됨
 *
 * @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly",value = "true"))
 * Member findReadOnlyByUsername(String username);
 *
 * ---> 하지만 힌트로 최적화는 거의 적다 - 이럴바엔 Redis 캐시를 이용한다
 * -> 복잡한 쿼리 자체가 문제다
 *
 * =========================
 * Lock - 실시간 접속이 많은 곳은 락을 걸면 안된다  - 접근이 안된다
 * -> OPTIMISTIC... 처럼 락을 쓰지 않고 하는 것이 좋다
 *
 * -> select for update
 * -> 비관적 락 - select 할때 다른 쓰레드가 접근하지 못하게 막는것
 *
 * @Lock(LockModeType.PESSIMISTIC_WRITE)
 * List<Member> findLockByUsername(String username);
 *
 * -> LockModeType.PESSIMISTIC_WRITE - javax.persistence 패키지 :JPA 에서 지원
 *
 * ------------------------------
 *
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
 * Impl 구현 클래스에서 설명 계속 ....
 *
 * ==========================================
 *
 * Auditing
 *
 * 엔티티를 생성, 변경할 때 변경한 사람과 시간을 추적하고 싶으면?
 * 등록일
 * 수정일
 * 등록자
 * 수정자
 *
 * -> 등록일, 수정일의 경우 운영상 꼭 필요하다 언제 어디서 문제가 났는지 추적해야한다
 *
 * ----------
 * 순수 JPA 사용
 *
 * JpaBaseEntity 클래스 확인
 * @MappedSuperclass
 * @PrePersist, @PostPersist
 * @PreUpdate, @PostUpdate
 * -----
 *
 * 스프링 데이터 JPA 사용
 * 설정
 * @EnableJpaAuditing 스프링 부트 설정 클래스에 적용해야함 - DataJpaPllication 클래스
 *
 * @EntityListeners(AuditingEntityListener.class) 엔티티에 적용 - DataJpaBaseEntityAuditing
 *
 * 사용 어노테이션
 * @CreatedDate
 * @LastModifiedDate
 * @CreatedBy
 * @LastModifiedBy
 *
 *
 * 주의: DataJpaApplication 에 @EnableJpaAuditing 도 함께 등록해야 합니다.
 * 실무에서는 세션 정보나, 스프링 시큐리티 로그인 정보에서 ID를 받음
 *
 * > 참고: 실무에서 대부분의 엔티티는 등록시간, 수정시간이 필요하지만, 등록자, 수정자는 없을 수도 있다. 그
 * 래서 다음과 같이 Base 타입을 분리하고, 원하는 타입을 선택해서 상속한다.
 *
 * > 참고: 저장시점에 등록일, 등록자는 물론이고, 수정일, 수정자도 같은 데이터가 저장된다. 데이터가 중복 저
 * 장되는 것 같지만, 이렇게 해두면 변경 컬럼만 확인해도 마지막에 업데이트한 유저를 확인 할 수 있으므로 유
 * 지보수 관점에서 편리하다. 이렇게 하지 않으면 변경 컬럼이 null 일때 등록 컬럼을 또 찾아야 한다.
 *
 * > 참고로 저장시점에 저장데이터만 입력하고 싶으면 @EnableJpaAuditing(modifyOnCreate = false)
 * 옵션을 사용하면 된다.
 *

 *
 * ---- 엔티티 관련 클래스
 @EntityListeners(AuditingEntityListener.class)
 @MappedSuperclass
 @Getter
 public class DataJpaBaseEntityAuditing {

 @CreatedDate
 @Column(updatable = false)
 private LocalDateTime createdDate;
 @LastModifiedDate
 private LocalDateTime lastModifiedDate;

 @CreatedBy
 @Column(updatable = false)
 private String createdBy;

 @LastModifiedBy
 private String lastModifiedBy;
 }

 --- 프로젝트 설정 클래스 DataJpaApplication
 @EnableJpaAuditing(modifyOnCreate = false) - update 는 null로 들어간다
 @EnableJpaAuditing
 @SpringBootApplication
 //@EnableJpaRepositories(basePackages = "study.data-jpa.repository")
 public class DataJpaApplication {

 public static void main(String[] args) {
 SpringApplication.run(DataJpaApplication.class, args);
 }


 //수정자, 생성자 넣는방법
 @Bean
 public AuditorAware<String> auditorProvider111(){
 //실무에서는 세션 정보나, 스프링 시큐리티 로그인 정보에서 ID를 받음 - 현재는 UUID를 임의로 사용
 return new AuditorAware<String>() {
 @Override
 public Optional<String> getCurrentAuditor() {
 return Optional.of(UUID.randomUUID().toString());
 }
 };
 //인터페이스에서 메서드 하나면 람다로 바꿀수있다
 //		return () -> Optional.of(UUID.randomUUID().toString());
 }
 }
-----------------------------

  * @EntityListeners(AuditingEntityListener.class) 를 생략하고 스프링 데이터 JPA 가 제공하는
 * 이벤트를 엔티티 전체에 적용하려면 orm.xml에 다음과 같이 등록하면 된다.
 *
    META-INF/orm.xml
    <?xml version=“1.0” encoding="UTF-8”?>
    <entity-mappings xmlns=“http://xmlns.jcp.org/xml/ns/persistence/orm”
    xmlns:xsi=“http://www.w3.org/2001/XMLSchema-instance”
    xsi:schemaLocation=“http://xmlns.jcp.org/xml/ns/persistence/
    orm http://xmlns.jcp.org/xml/ns/persistence/orm_2_2.xsd”
    version=“2.2">
    <persistence-unit-metadata>
    <persistence-unit-defaults>
    <entity-listeners>
    <entity-listener
    class="org.springframework.data.jpa.domain.support.AuditingEntityListener”/>
    </entity-listeners>
    </persistence-unit-defaults>
    </persistence-unit-metadata>

    </entity-mappings>


--------------------
*
 * 실무에서는 어떤곳에는 일자만, 어떤곳에는 수정자 까지 ,,,
 * 구분할때??
 *
 * 최상위
 * BaseTime
 *
 * 최상위 상속받은 속성
 * baseEntity - 생성자
 *
 *
  * */



public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor{

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    //by 뒤에 생략시 조건없이 모두 가져온다
    List<Member> findHelloBy();

    //JPQL에 파라미터가 있을때는 NamedParam이 필요해서 @Param을 사용한다
//    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

//    @Query, 리포지토리 메소드에 쿼리 정의하기
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

//------------------------------
//  @Query, 값,
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    //  DTO 조회하기 - 마치 생성자로 new 하는것처럼 매칭해서 적어줘야한다
    @Query("select new study.datajpa.dto.MemberDto( m.id, m.username, t.name ) from Member m join m.team t")
    List<MemberDto> findMemberDto();
//---------------------------------------

//    컬렉션 파라미터 바인딩 Collection 타입으로 in절 지원 - 알아서 () , 등 처리가 이루어진다
    @Query("select m from Member m where m.username in :names ")
    List<Member> findByNames(@Param("names") Collection<String> names);

    //----------------------------
    //반환 타입
    List<Member> findListByUsername(String username); // 컬렉션 - 반환 값이 없을때 빈 컬렉션을 반환해준다 - size = 0
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); //단건 optional

    //---------------------
    //페이징

    //pageable - 현재 페이지
    // 반환타입이 Page 이면 totalcount 쿼리까지 같이 날라간다다
    //countQuery 를 조인없이 성능을 최적화 할수 있다
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);
    // slice 는 3개를 요청하면 limit 에 +1 해서 4개를 가져온다 - 전체를 가져오지 않는다 - totalcount 쿼리 없이 사용한다
    Slice<Member> findSliceByAge(int age, Pageable pageable);
    List<Member> findListByAge(int age, Pageable pageable); // 단순히 몇개 가져와라 정도로 사용할때 페이징 불가

    //-------------------
    //벌크성 쿼리
    @Modifying(clearAutomatically = true) // - 순수 JPA 의 executeUpdate 와 같다
    @Query(value = "update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    //------------------
    //EntityGraph, 페치 조인
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    //상속받은 JpaRepository 에 선언되어있는(구현은 SpringDataJpa가 해준다 )
    //findAll을 override 한다
    @Override
    @EntityGraph(attributePaths = {"team"}) // 필드명 적어준다
    List<Member> findAll();

    //쿼리 + 엔티티그래프
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    //메서드 명으로 가져올때 조인 거는법
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraph11ByUsername(@Param("username")String username);

    //순수 jpa 엔티티에서 entitygraph 사용법
    @EntityGraph("Member.all")
    List<Member> findEntityJpaByUsername(@Param("username") String username);

    //-----------------------
    //JPA Hint & LocK
    //100% 조회용으로만 쓸경우 최적화 - hibernate는 제공하는데 jpa 는 제공 안함 그래서 hint를 줄수 있게 함
    //아무거나 hibernate 에 넘길수 있게 열어둔 것
    //readOnly - true 가 되어있으면 스냅샷 안만들어 버린다 - 최적화 되어 변경 안됨
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly",value = "true"))
    Member findReadOnlyByUsername(String username);

    //-------------------------
    //Lock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);


    //---------section 7 projection
    List<UsernameOnly> findProjectionByUsername(@Param("username") String username);

    List<UsernameOnlyDto> findProjectionClassByUsername(@Param("username") String username);
    //동적 projection -  //중첩구조 projection 테스트 가능
    <T> List<T> findProjectionClassTypeByUsername(@Param("username") String username,Class<T> type);

    //-----네이티브쿼리
    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);
    @Query(value = "select username from member where username = ?", nativeQuery = true)
    Member findByNativeQuery2(String username);

    //projection을 통한 네이티브 쿼리
    @Query(value = "select m.member_id as id, m.username, t.name as teamName " +
            " from member m left join team t "
            , countQuery = "select count(*) from member", nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
