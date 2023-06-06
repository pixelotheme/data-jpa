package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;


@NamedQuery(
        name = "Member.findByUsername",
        query = "select m from Member m where m.username = :username "
        )
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본생성자 protected level 까지만 가능
@ToString(of = {"id", "username", "age"}) // 연관관계 team엔티티 넣으면 무한 루프 돌수  있어 제거
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;


    public Member( String username) {
        this.username = username;
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if(team != null){
            changeTeam(team);
        }
    }


    public void changeUserName(String username){
        this.username = username;
    }

    // =======연관관계 편의 메서드 ============
    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }
}
