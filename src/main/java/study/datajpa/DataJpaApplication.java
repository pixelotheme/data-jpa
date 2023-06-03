package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JavaConfig 설정- 스프링 부트 사용시 생략 가능
 *
 * @Configuration
 * @EnableJpaRepositories(basePackages = "jpabook.jpashop.repository")
 * public class AppConfig {}
 *
 * 스프링 부트 사용시 @SpringBootApplication 어노테이션의 위치를 자동으로 지정(해당 패키지와 하위 패키지 인식)
 * -> 만약 위치가 달라지면 @EnableJpaRepositories 필요
 *
 * */

@SpringBootApplication
//@EnableJpaRepositories(basePackages = "study.data-jpa.repository")
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

}
