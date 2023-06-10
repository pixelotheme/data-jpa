package study.datajpa.section5_domain_convertor_controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.section1_4_repository.MemberRepository;

import javax.annotation.PostConstruct;

/**
 * - section5 -
 *
 * Web 확장 - 도메인 클래스 컨버터
 *
 * HTTP 파라미터로 넘어온 엔티티의 아이디로 엔티티 객체를 찾아서 바인딩
 *
 * -> 메서드 정의
 *  public String findMember2(@PathVariable("id") Member member)
 *
 *  -> 사실 권장하진 않는다 PK를 외부에 공개해서 단순하게 통신하는경우가 많지않다
 *  ... 조금만 복잡해져도 사용하지 못한다
 *
 * HTTP 요청은 회원 id 를 받지만 도메인 클래스 컨버터가 중간에 동작해서 회원 엔티티 객체를 반환
 * 도메인 클래스 컨버터도 리파지토리를 사용해서 엔티티를 찾음
 *
 * > 주의: 도메인 클래스 컨버터로 엔티티를 파라미터로 받으면, 이 엔티티는 단순 조회용으로만 사용해야 한다.
 * (트랜잭션이 없는 범위에서 엔티티를 조회했으므로, 엔티티를 변경해도 DB에 반영되지 않는다.)
 *
 *
 * ===================
 *
 * Web 확장 - 페이징과 정렬
 *
 * Pageable - section4 를 찾아보자 - 현재페이지, 가져올 컨텐츠 개수 가 들어있다
 *  URL 요청 : http://localhost:8001/members?page=1&size=3
 *  - page : 현재페이지, size : 표시할 컨텐츠 개수
 *
 *
 *     @GetMapping("/members")
 *     public Page<Member> list(Pageable pageable){
 *         return memberRepository.findAll(pageable);
 *     }
 *
 * 파라미터로 Pageable 을 받을 수 있다.
 * Pageable 은 인터페이스, 실제는 org.springframework.data.domain.PageRequest 객체 생성
 *
 * - 요청 파라미터
 * 예) /members?page=0&size=3&sort=id,desc&sort=username,desc
 * page: 현재 페이지, 0부터 시작한다.
 * size: 한 페이지에 노출할 데이터 건수
 * sort: 정렬 조건을 정의한다. 예) 정렬 속성,정렬 속성...(ASC | DESC), 정렬 방향을 변경하고 싶으면 sort
 * 파라미터 추가 ( asc 생략 가능)
 *
 * --- 기본값 변경 설정
 *
 * - 글로벌 설정: 스프링 부트 - yml
 * spring.data.web.pageable.default-page-size=20 /# 기본 페이지 사이즈/
 * spring.data.web.pageable.max-page-size=2000 /# 최대 페이지 사이즈/
 * ... 나머지는 스프링 부트 메뉴얼 참고 ...
 *
 * - 개별설정 - 메서드 파라미터에 어노테이션 붙여준다
 *     //개별 페이징 default 설정
 *     @GetMapping("/members")
 *     public Page<Member> list2(@PageableDefault(size = 5, sort = "username") Pageable pageable){
 *         return memberRepository.findAll(pageable);
 *     }
 *
 * ---접두사
 * 페이징 정보가 둘 이상이면 접두사로 구분
 * @Qualifier 에 접두사명 추가 "{접두사명}_xxx”
 * 예제: /members?member_page=0&order_page=1
 * public String list(
 *  @Qualifier("member") Pageable memberPageable,
 *  @Qualifier("order") Pageable orderPageable, ...
 *  )
 *
 * -------------
 * Page 내용을 DTO로 변환하기
 *
 * 엔티티를 API로 노출하면 다양한 문제가 발생한다.
 * 그래서 엔티티를 꼭 DTO로 변환해서 반환해야 한다.
 * Page는 map() 을 지원해서 내부 데이터를 다른 것으로 변경할 수 있다.
 *
 * Page<Member> page = memberRepository.findAll(pageable);
 * Page<MemberDto> memberDto = page.map(member -> new MemberDto(member));
 * //Page<MemberDto> memberDto = page.map(MemberDto::new);
 *
 *
 * ----------------------
 * Page를 1부터 시작하기
 *
 * 스프링 데이터는 Page를 0부터 시작한다.
 * 만약 1부터 시작하려면?
 *
 * 1. Pageable, Page를 파리미터와 응답 값으로 사용히지 않고, 직접 클래스를 만들어서 처리한다.
 * 그리고 직접 PageRequest(Pageable 구현체)를 생성해서 리포지토리에 넘긴다.
 * 물론 응답값도 Page 대신에 직접 만들어서 제공해야 한다.
 *
 * 2. spring.data.web.pageable.one-indexed-parameters 를 true 로 설정한다.
 * 그런데 이 방법은 web에서 page 파라미터를 -1 처리 할 뿐이다.
 * 따라서 응답값인 Page 에 모두 0 페이지 인덱스를 사용하는 한계가 있다
 * -> postman으로 확인해보면 page=2 라고 해도 실제 pageable의 pageNumber는 1로 나온다
 *
 * -> 그냥 0부터 시작하는것으로 사용하자
 *
 * */
@RestController //@Controller @ResponseBody 두개를 합친 RestAPI 스타일로 만든다는 어노테이션
//ResponseBody 데이터 자체를 바로 JSON, XML 로 바로 보낸다
@RequiredArgsConstructor
public class MemberController {


    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id){
        Member member = memberRepository.findById(id).get();

        return member.getUsername();
    }
    //spring data jpa 가 바로 member 찾아와 주입해준다
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member){
        return member.getUsername();
    }

    //---------------------------------//
    //페이징과 정렬
    //Pageable - section4 를 찾아보자 - 현재페이지, 가져올 컨텐츠 개수 가 들어있다
    // URL 요청 : http://localhost:8001/members?page=1&size=3
    // - page : 현재페이지, size : 표시할 컨텐츠 개수

    @GetMapping("/members")
    public Page<Member> list(Pageable pageable){
        return memberRepository.findAll(pageable);
    }
    //개별 페이징 default 설정
    @GetMapping("/members2")
    public Page<Member> list2(@PageableDefault(size = 5, sort = "username") Pageable pageable){
        return memberRepository.findAll(pageable);
    }
    //Page 안의 Member 엔티티 -> DTO 로 변환
    @GetMapping("/members3")
    public Page<MemberDto> list3(@PageableDefault(size = 5, sort = "username") Pageable pageable){
        Page<Member> page = memberRepository.findAll(pageable);
//        Page<MemberDto> memberDto = page.map(member -> new MemberDto(member));
        Page<MemberDto> memberDto = page.map(MemberDto::new);

        return memberDto;
    }

    //Controller 빈 생성후 실행
//    @PostConstruct
//    public void init(){
//        for (int i = 0 ; i < 100; i++){
//            memberRepository.save(new Member("userA"+i, i));
//
//        }
//    }
}
