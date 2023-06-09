package study.datajpa.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

/**
 * 순수 JPA 사용
 * */
@Getter
@Setter
@MappedSuperclass // 엔티티에 속성만 상속받게 하고싶을때 사용
public class JpaBaseEntityAuditing {

    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;

    @PrePersist // 영속성 전에 실행
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updateDate = now; // 데이터를 넣어야 쿼리할때 편하다 ~ null있으면 불편~
    }

    @PreUpdate
    public void preUpdate(){
        updateDate = LocalDateTime.now();
    }
}
