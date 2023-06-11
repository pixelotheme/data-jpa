package study.datajpa.section7_other_features;

/**네이티브쿼리 + projection*/
public interface MemberProjection {

    Long getId();
    String getUsername();
    String getTeamName();
}
