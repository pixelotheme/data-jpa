package study.datajpa.section7_other_features;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.criteria.*;

public class MemberSpec {

    public static Specification<Member> teamName(final String teamName){
        return new Specification<Member>() {
            //root 처음 선택한 엔티티
            @Override
            public Predicate toPredicate(Root<Member> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                if(StringUtils.isEmpty(teamName)){
                    return null;
                }

                Join<Member, Team> t = root.join("team", JoinType.INNER);//회원과 조인
                return criteriaBuilder.equal(t.get("name"), teamName); //where 문만들어 진다
            }
        };

    }

    public static Specification<Member> username(final String username){
        return new Specification<Member>() {
            //root 처음 선택한 엔티티
            @Override
            public Predicate toPredicate(Root<Member> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                return criteriaBuilder.equal(root.get("username"), username);
            }
        };
    }
}
