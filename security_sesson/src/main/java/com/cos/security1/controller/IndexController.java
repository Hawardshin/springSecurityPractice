package com.cos.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Controller //viwe 를 리턴하겠다.
public class IndexController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/test/login")
	//AuthenticationPrincipal 어노테이션을 사용하면 세션 정보를 바로 받아올 수 있다.
	public @ResponseBody String testLogin(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails){ //DI(의존성 주입)
		System.out.println("/test/login =============");
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		System.out.println("authentication = " + principalDetails.toString());
		System.out.println("authentication = " + principalDetails.getUsername());
		System.out.println("userDetails = " + userDetails.getUsername());
		return "세션 정보 확인하기";
	}

	@GetMapping("/test/oauth/login")
	//AuthenticationPrincipal 어노테이션을 사용하면 세션 정보를 바로 받아올 수 있다.
	// 이런식으로 두개 분기할 필요 없이 아래쪽에 하나의 객체를 사용하면 통합 가능
	public @ResponseBody String testOAuthLogin(Authentication authentication,
		@AuthenticationPrincipal OAuth2User oAuth){ //DI(의존성 주입)
		System.out.println("/test/oauth/login =============");
		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		System.out.println("authentication = " + oAuth2User.getAttributes());
		System.out.println("oAuth2User = " + oAuth.getAttributes());
		// super.loadUser(userRequest).getAttributes());
		return "OAuth 세션 정보 확인하기";
	}
	@GetMapping({"","/"})
	public String index(){
		//머스테치 기본폴더 src/main/resources/
		//뷰리졸버 설정 : templates(prefix), .mustache(suffix) 생략가능
		return "index"; //src/main/resources/templates/index.mustache
	}

	//통합됐다.
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("principalDetails = " + principalDetails.getUser());
		return "user";
	}

	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}

	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}

	@GetMapping("/loginForm") //스프링 시큐리티가 해당 주소를 낚아챔. SecurityConfig 파일 생성 후 작동안함.
	public  String loginForm() {
		return "loginForm";
	}

	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}

	@PostMapping("/join")
	public String join(User user) {
		System.out.println("user = " + user);
		user.setRole("ROLE_USER");
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		userRepository.save(user);//회원가입 잘 됨 (비밀번호  1234로 들어가는데 이렇게 하면 시큐리티 로그인 불가. 해쉬암호화 해야함)
		return "redirect:/loginForm";
	}

	@Secured("ROLE_ADMIN")
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인정보";
	}

	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
	// @PostAuthorize() //이건 메서드가 끝난 후에 실행됨 잘 안 씁니다.
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "데이터 정보";
	}
}
