package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;
import java.util.UUID;

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
 * --------------
 * 시작일, 수정일  springDataJpa 관련 어노테이션
 * @EnableJpaAuditing
 *
 * */
//@EnableJpaAuditing(modifyOnCreate = false) - update 는 null로 들어간다
@EnableJpaAuditing
@SpringBootApplication
//@EnableJpaRepositories(basePackages = "study.data-jpa.repository")
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}


	//수정자, 생성자 넣는방법
	@Bean
	public AuditorAware<String> auditorProvider111(){
		//실무에서는 세션 정보나, 스프링 시큐리티 로그인 정보에서 ID를 받음 - 현재는 UUID를 임의로 사용
		return new AuditorAware<String>() {
			@Override
			public Optional<String> getCurrentAuditor() {
				return Optional.of(UUID.randomUUID().toString());
			}
		};
		//인터페이스에서 메서드 하나면 람다로 바꿀수있다
//		return () -> Optional.of(UUID.randomUUID().toString());
	}
}
