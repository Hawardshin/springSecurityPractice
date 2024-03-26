package com.cos.security1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 된다.
public class SecurityConfig {

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
			login.loginPage("/login"); // 로그인 페이지를 따로 만들지 않았기 때문에 스프링 시큐리티가 제공하는 로그인 페이지로 이동
			// login.permitAll();
			}
		);

		return http.build();
	}

}
