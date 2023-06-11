package study.datajpa.section7_other_features;
/**
 *
 * 클래스 기반 Projection - 구체적인 클래스를 지정했기때문에 프록시 기술이 없어도 된다
 *
 * 다음과 같이 인터페이스가 아닌 구체적인 DTO 형식도 가능
 * 생성자의 파라미터 이름으로 매칭 - 파라미터 명이 달라지면 된다
 *
 * */
public class UsernameOnlyDto {

    private final String username;

    //생성자 파라미터 명으로 프로젝션이 구동된다
    public UsernameOnlyDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
