package study.datajpa.section1_4_repository;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * 복잡도를 낮추기위해 별도의 Repository를 만들어 줬다
 *
 * */


@Repository
@RequiredArgsConstructor
public class MemberQueryRepository_5 {

    private final EntityManager em;

    List<Member> findAllMembers(){
        return em.createQuery("select m from Member m").getResultList();
    }
}
