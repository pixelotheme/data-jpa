package study.datajpa.section1_4_repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

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
 *  org.springframework.data.domain.Page : 추가 count 쿼리 결과를 포함하는 페이징
 *  -> totalcount 필요한 페이징
 *
 *  org.springframework.data.domain.Slice : 추가 count 쿼리 없이 다음 페이지만 확인 가능(내부적으로 limit + 1조회)
 *  -> 모바일 에서 10개씩 나오고 더보기 버튼 누르면 10개 더나올때 사용
 *  -> 눈속임으로 11개 가져온뒤 11번째가 있으면 더보기 버튼 생김, 없으면 마지막페이지
 *
 *  List (자바 컬렉션): 추가 count 쿼리 없이 결과만 반환
 *
 *
 * ========
 * Pagealbe 파라미터의 구현체 사용법
 *
 * 두 번째 파라미터로 받은 Pageable 은 인터페이스다. 따라서 실제 사용할 때는 해당 인터페이스를 구현한
 * org.springframework.data.domain.PageRequest 객체를 사용한다.
 * PageRequest 생성자의 첫 번째 파라미터에는 현재 페이지를, 두 번째 파라미터에는 조회할 데이터 수를
 * 입력한다. 여기에 추가로 정렬 정보도 파라미터로 사용할 수 있다. 참고로 페이지는 0부터 시작한다.
 *
 * > 주의: Page는 1부터 시작이 아니라 0부터 시작이다.
 *
 * ========================
 * 사용 방법
 * Page 타입 리턴일때
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
 *         for (Member member : content) {
 *             System.out.println("member = " + member);
 *         }
 *         System.out.println("totalElements = " + totalElements);
 *
 *         Assertions.assertThat(content.size()).isEqualTo(3);
 *         Assertions.assertThat(page.getTotalElements()).isEqualTo(6);
 *         // 페이지 번호
 *         Assertions.assertThat(page.getNumber()).isEqualTo(0);
 *         //총 페이지 개수
 *         Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
 *         //첫 페이지인지 ?
 *         Assertions.assertThat(page.isFirst()).isEqualTo(true);
 *         //다음 페이지가 있는지?
 *         Assertions.assertThat(page.hasNext()).isEqualTo(true);
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
 * */


public interface MemberRepository extends JpaRepository<Member, Long> {

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
}
