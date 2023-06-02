package study.datajpa.entity;

import lombok.Generated;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    protected Member(){

    }

    public Member( String username) {
        this.username = username;
    }

    public void changeUserName(String username){
        this.username = username;
    }
}
