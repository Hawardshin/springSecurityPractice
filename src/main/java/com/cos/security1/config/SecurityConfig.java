package com.cos.security1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 된다.
public class SecurityConfig {

	//해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다.
	@Bean
	public BCryptPasswordEncoder encodePwd(){
		return new BCryptPasswordEncoder();
	}

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

		return http.build();
	}

}
