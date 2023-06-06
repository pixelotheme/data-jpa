package study.datajpa.dto;

import lombok.Data;

@Data // 엔티티안에서는 쓰면 안된다 ... getter, setter.... 다들어가 있다
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }
}
