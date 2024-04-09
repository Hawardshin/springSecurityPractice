package com.cos.security1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;

//1. 코드받기(인증이 됐다.), 2. 코드를 통해서 엑세스토큰을 받고(사용자 정보에 접근 권한이 생깁니다.), 3. 사용자프로필 정보를 가져오고
//4-1. 그 정보를 토대로 회원가입을 자동으로 진행시키기도 한다. (이메일, 전화번호, 이름, 아이디)
//4-2. (직접 회원가입을 하는 경우) 추가적인 회원가입 창이 나온다.

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 된다.
@EnableMethodSecurity(securedEnabled = true,prePostEnabled = true) //secured, preAuthorize, PostAuthorize 어노테이션 활성화
public class SecurityConfig {

	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;

	//해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다.
	// @Bean
	// public BCryptPasswordEncoder encodePwd(){
	// 	return new BCryptPasswordEncoder();
	// }

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(CsrfConfigurer::disable); // csrf 토큰 비활성화 (테스트시 걸어두는게 좋음
		http.authorizeHttpRequests(authorize ->
			authorize
				.requestMatchers("/user/**").authenticated()// 인증만 되면 들어갈 수 있는 주소
				.requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN") //인증은 물론이고, MANAGER, ADMIN 권한이 있어야 들어갈 수 있는 주소
				.requestMatchers("/admin/**").hasAnyRole("ADMIN") //인증은 물론이고, ADMIN 권한이 있어야 들어갈 수 있는 주소
				.anyRequest().permitAll() //위의 주소를 제외한 나머지 주소는 누구나 들어갈 수 있다.
		);
		http.formLogin(login ->{
			login
				.loginPage("/loginForm")
				.loginProcessingUrl("/login") //login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해 준다.(컨트롤러에 /login을 만들지 않아도 된다.)
				.defaultSuccessUrl("/");
			}
		);
		//oauth2 로그인
		http.oauth2Login(oauth2 -> {
			oauth2
				.loginPage("/loginForm")
				.userInfoEndpoint(userInfo -> {
					userInfo.userService(principalOauth2UserService); // 소셜로그인 성공 이후 후처리를 진행할 서비스 인터페이스의 구현체를 등록한다.
				});
			//로그인이 완료된 이후의 후처리가 필요하다. Tip. 코드를 받는게 아니라, (엑세스토큰+사용자프로필정보를 한번에 받습니다.)라이브러리의 장점

		});


		return http.build();
	}

}
