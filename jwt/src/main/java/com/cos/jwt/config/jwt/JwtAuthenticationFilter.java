package com.cos.jwt.config.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

//스프링 시큐리티에서 UsernamePasswordAuthenticationFilter 가 있다.
//login 요청해서 username, password 전송하면 (post)
// UsernamePasswordAuthenticationFilter 동작을 한다.
//근데 지금은 security config 에서 form 로그인을 disable 했기 때문에 이 필터가 작동하지 않는다.

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager authenticationManager;


	//authenticationManager 를 이용해서 로그인을 시도하는데 그 때 실행되는 함수가 이 attemptAuthentication 함수이다.
	//즉 /login 요청을 하면 로그인 시도를 위해서 실행되는 함수이다.

	//login 요청을 하면 로그인 시도를 위해서 실행되는 함수
	// 1. username, password 받아서
	// 2. 정상인지 로그인 시도를 해봄. authenticationManager로 로그인 시도를 하면 PrincipalDetailsService가 호출이 된다.
	// 3, 그러면 PrincipalDetailsService에서 loadUserByUsername() 함수가 실행이 된다.

	// 4. PrincipalDetailsService에서 return new PrincipalDetails(userEntity); 를 하게 되면
	// 5. PrincipalDetails를 세션에 담고 (권한 관리를 위해서)

	// 6. JWT 토큰을 만들어서 응답해주면 됨.
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {
		System.out.println("JwtAuthenticationFilter 로그인 시도중");
		return super.attemptAuthentication(request, response);
	}
}
