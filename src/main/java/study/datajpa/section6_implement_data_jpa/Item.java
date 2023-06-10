package study.datajpa.section6_implement_data_jpa;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * 스프링 데이터 JPA 분석
 *
 * 스프링 데이터 JPA 구현체 분석
 *
 * 스프링 데이터 JPA가 제공하는 공통 인터페이스의 구현체
 * org.springframework.data.jpa.repository.support.SimpleJpaRepository
 *
 *
 * 리스트 12.31 SimpleJpaRepository
 *
 * @Repository
 * @Transactional(readOnly = true)
 * public class SimpleJpaRepository<T, ID> ...{
 *     @Transactional
 *     public <S extends T> S save(S entity) {
 *         if (entityInformation.isNew(entity)) {
 *             em.persist(entity);
 *             return entity;
 *         } else {
 *             return em.merge(entity);
 *         }
 *     }
 *     ...
 * }
 *
 * @Repository 적용: JPA 예외를 스프링이 추상화한 예외로 변환
 * -> 영속성 계층의 예외는 jpa 와 spring 예외가 달라
 * -> service, controller 계층의 에외는 jpa 가 아닌 spring 예외 발생
 * -> 하부 기술을 JDBC -> JPA 로 바꿀경우 예외 처리 가 같아
 * -> 핵심적 기능의 손상을 줄이도록 한다
 *
 * @Transactional 트랜잭션 적용
 * - JPA의 모든 변경은 트랜잭션 안에서 동작
 * - 스프링 데이터 JPA는 변경(등록, 수정, 삭제) 메서드를 트랜잭션 처리
 * - 서비스 계층에서 트랜잭션을 시작하지 않으면 리파지토리에서 트랜잭션 시작 - DataJPA를 사용하면 트랜잭션이 자동으로 걸린다
 * - 서비스 계층에서 트랜잭션을 시작하면 리파지토리는 해당 트랜잭션을 전파 받아서 사용 - 연쇄적으로  들어간다
 * - 그래서 스프링 데이터 JPA를 사용할 때 트랜잭션이 없어도 데이터 등록, 변경이 가능했음
 * (사실은 트랜잭션이 리포지토리 계층에 걸려있는 것임)
 *
 * @Transactional(readOnly = true)
 * - 데이터를 단순히 조회만 하고 변경하지 않는 트랜잭션에서 readOnly = true 옵션을 사용하면
 * 플러시를 생략해서 약간의 성능 향상을 얻을 수 있음
 * - 자세한 내용은 JPA 책 15.4.2 읽기 전용 쿼리의 성능 최적화 참고
 *
 * -> jdbc 메커니즘 - @Transactional(readOnly = true)적용시 DBconnection에 setAutoCommit - false 로 넘긴다
 * - 일반적인 트랜잭션 얻는것과 똑같다, 다만 플러시를 안함
 *
 *
 * -------------
 * 매우 중요!!!
 * * save() 메서드*
 *     새로운 엔티티면 저장( persist )
 *     새로운 엔티티가 아니면 병합( merge ) - merge 작동방식 - DB에서 같은 데이터 가져와 덮어씌운다(성능 저하)
 *     -> merge는 영속상태를 벗어났다가 다시 영속상태가 된 컨텍스트일때 사용해야한다
 *
 * 새로운 엔티티를 판단하는 기본 전략 - em.persist 할때 PK값이 들어간다 (@GeneratedValue 의 기능)
 * 1. 식별자가 객체일 때 null 로 판단
 * 2. 식별자가 자바 기본 타입일 때 0 으로 판단
 * 3. Persistable 인터페이스를 구현해서 판단 로직 변경 가능 ---> 문제점 해결방안
 *
 * @Transactional
 * @Override
 * public <S extends T> S save(S entity) {
 *
 *     Assert.notNull(entity, "Entity must not be null.");
 *
 *     if (entityInformation.isNew(entity)) {
 *         em.persist(entity);
 *         return entity;
 *     } else {
 *         return em.merge(entity);
 *     }
 * }
 *
 * **=====================
 * 문제점
 *     @Id
 *     private String id;
 * 직접 PK값을 주입해 줄때 문제가 생긴다
 * - PK 값이 있다고 보고 merge 를 통해 실행된다
 * -> 3. Persistable 인터페이스를 구현해서 판단 로직 변경 가능
 *
 *
 * ----------클래스 에 오버라딩
 * @Entity
 * @EntityListeners(AuditingEntityListener.class)
 * @NoArgsConstructor(access = AccessLevel.PROTECTED)
 * public class Item implements Persistable<String> {}
 *
 *     @CreatedDate
 *     private LocalDateTime createdDate;
 *
 *     //새로운 객체 구분
 *     @Override
 *     public boolean isNew() {
 *         return createdDate == null;
 *     }
 *
 *
 * > 참고: JPA 식별자 생성 전략이 @GenerateValue 면 save() 호출 시점에 식별자가 없으므로 새로운 엔티
 * 티로 인식해서 정상 동작한다.
 * 그런데 JPA 식별자 생성 전략이 @Id 만 사용해서 직접 할당이면 이미 식별자 값이 있는 상태로 save() 를 호출한다.
 *
 * 따라서 이 경우 merge() 가 호출된다. merge() 는 우선 DB를 호출해서 값을 확인하고,
 * DB에 값이 없으면 새로운 엔티티로 인지하므로 매우 비효율 적이다.
 * 따라서 Persistable 를 사용해서 새로운 엔티티 확인 여부를 직접 구현하게는 효과적이다.
 *
 * > 참고로 등록시간( @CreatedDate )을 조합해서 사용하면 이 필드로 새로운 엔티티 여부를 편리하게 확인할
 * 수 있다. (@CreatedDate에 값이 없으면 새로운 엔티티로 판단)
 *
 * */

//@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {

    //    @Id @GeneratedValue
//    private Long id;
    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdDate;

    public Item(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
    //새로운 객체 구분
    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}
