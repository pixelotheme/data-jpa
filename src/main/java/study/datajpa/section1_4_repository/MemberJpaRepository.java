package study.datajpa.section1_4_repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository // 컴포넌트 스캔 대상
public class MemberJpaRepository {

    //EntityManager를 사용하기 위해 써준다
    @PersistenceContext
    private EntityManager em;


    public Member save(Member member){
        em.persist(member);
        return member;
    }

    public void delete(Member member){
        em.remove(member);
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public Optional<Member> findById(Long id){
        Member member = em.find(Member.class, id);
        //ofNullable - null 일수도 있다
        return Optional.ofNullable(member);
    }

    public long count(){
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();
    }

    public Member find(Long id){
        return em.find(Member.class, id);
    }

    public List<Member> findByUsernameAndAgeGreaterThen(String username, int age){
        return em.createQuery("select m from Member m where m.username = :username and m.age > :age", Member.class)
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    //NamedQuery 테스트
    public List<Member> findByUsername(String username){
        return em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", username)
                .getResultList();
    }


    //페이징
    public List<Member> findByPage(int age, int offset, int limit){
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc")
                .setParameter("age", age)
                .setFirstResult(offset) //몇번째 부터 가져오는지?
                .setMaxResults(limit)// 몇개 가져오는지?
                .getResultList();
    }
    // 페이징 content 개수 - 몇번째 페이지 인지 구분하기위해
    public long totalCount(int age){
        return em.createQuery("select count(m) from Member m where m.age = :age" , Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

    //벌크성 쿼리 - 순수 JPA - executeUpdate() 사용
    public int bulkAgePlus(int age){
        int resultCnt = em.createQuery("update Member m set m.age = m.age + 1 where m.age >= :age")
                .setParameter("age", age)
                .executeUpdate();
        return resultCnt;
    }
}
