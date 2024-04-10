package com.cos.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import com.cos.jwt.config.jwt.JwtAuthenticationFilter;
import com.cos.jwt.config.jwt.JwtAuthorizationFilter;
import com.cos.jwt.repository.UserRepository;

import filter.MyFilter3;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CorsFilter corsFilter;
	//Spring Security 5.2 이상에서 AuthenticationManager를 가져오는 방법이 변경되었음
	private final AuthenticationConfiguration authenticationConfiguration;
	private final UserRepository userRepository;
	//jwt 기본 설정
	// MyFilter3 doFilter
	// MyFilter2 doFilter
	// MyFilter1 doFilter
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// http.addFilterBefore(new MyFilter3(), BasicAuthenticationFilter.class); // 이렇게 필터를 추가하면 됩니다.

		// http.addFilter(new MyFilter1()); //이렇게 필터를 추가하면 오류가 납니다. (여기선 시큐리터 필터만 가능하기 때문에)
		// http.addFilterBefore(new MyFilter1(), BasicAuthenticationFilter.class); // 이렇게 필터를 추가하면 됩니다.
		//이것의 의미는 BasicAuthenticationFilter가 실행되기 전에 MyFilter1을 실행하겠다는 의미입니다.
		System.out.println("필터체인");
		// AuthenticationManager authenticationManager =  http.getSharedObject(AuthenticationManager.class);
		AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
		System.out.println("매니저:::::" + authenticationManager);
		http.csrf(CsrfConfigurer::disable); // csrf 토큰 비활성화 (테스트시 걸어두는게 좋음)
		http.sessionManagement(sesson->{
			sesson.sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 세션을 사용하지 않겠다.
		});
		http.addFilter(corsFilter); // @CrossOrigin(인증X), 시큐리티 필터에 등록 인증(O
		http.formLogin(FormLoginConfigurer::disable); // form 로그인 비활성화
		http.httpBasic(HttpBasicConfigurer::disable); // httpBasic 비활성화
		http.addFilter(new JwtAuthenticationFilter(authenticationManager))
		.addFilter(new JwtAuthorizationFilter(authenticationManager,userRepository)); //jwt를 위한 필터를 추가
		http.authorizeHttpRequests(authorize ->
			authorize
				.requestMatchers("/api/v1/user/**")
				.hasAnyRole("USER", "ADMIN", "MANAGER") //인증은 물론이고, USER, ADMIN 권한이 있어야 들어갈 수 있는 주소
				.requestMatchers("/api/v1/manager/**")
				.hasAnyRole("ADMIN", "MANAGER") //인증은 물론이고, USER, ADMIN 권한이 있어야 들어갈 수 있는 주소
				.requestMatchers("/api/v1/admin/**")
				.hasAnyRole("ADMIN") //인증은 물론이고, ADMIN 권한이 있어야 들어갈 수 있는 주소
				.anyRequest()
				.permitAll() //위의 주소를 제외한 나머지 주소는 누구나 들어갈 수 있다.
		);
		return http.build();
	}
}
