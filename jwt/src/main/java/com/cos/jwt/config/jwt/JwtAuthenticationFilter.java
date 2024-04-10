package com.cos.jwt.config.jwt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwt.config.auth.PrincipalDetails;
import com.cos.jwt.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

		// 1. username, password 받아서

		//리퀘스트의 바디를 확인하고 싶다면 이렇게 하면 된다.
		// BufferedReader br = request.getReader();
		// String input = null;
		// while((input = br.readLine()) != null) {
		// 	System.out.println(input);
		// }
		User user = null;
		try {
			ObjectMapper om = new ObjectMapper(); //json 데이터 파싱해 줌
			user = om.readValue(request.getInputStream(), User.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.out.println(user);

		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

		//이게 실행될 때 PrincipalDetailsService의 loadUserByUsername()이 호출이 된다.
		//authentication 에 내 로그인 한 정보가 담긴다.
		System.out.println("authenticationToken = " + authenticationToken);
		// Authentication authentication;
		// try{
			//로그인 시도를 위해서 authenticationManager로 로그인 시도를 하면 PrincipalDetailsService가 호출이 된다.
			//그러면 PrincipalDetailsService에서 loadUserByUsername() 함수가 실행이 된다.
			//이후 함수가 실행 후 정상이면 authentication 객체가 리턴된다.
			//정상적인 로그인이라면 authentication 객체가 리턴된다.
			//즉 db안의 username과 password가 일치한다는 뜻이다.
		Authentication authentication = authenticationManager.authenticate(authenticationToken);
		// }
		// catch (AuthenticationException e){
		// 	System.out.println("e = " + e);
		// 	throw new RuntimeException(e);
		// }
		System.out.println("what?");
		System.out.println("authentication = " + authentication.getPrincipal());
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		System.out.println(principalDetails.getUser().getUsername()); //로그인이 되었다는 것은 세션에 저장이 되었다는 것이다.
		System.out.println("===================================");
		//authentication 객체가 session 영역에 저장되었다 (리턴될때)
		// 리턴해주는 이유는 권한 관리를 시큐리티가 대신 해주기 때문에 편하게 하려고 하는 것이다.
		// 굳이 JWT 토큰을 사용하면서 세션을 만들 이유가 없지만 단지 권한 처리때문에 session에 넣어준다.

		return authentication; //이게 리턴되면 실행되는 함수가 있다. successfulAuthentication()
	}

	//attemptAuthentication 실행 후 인증이 정상적으로 되었으면 successfulAuthentication 함수가 실행된다.
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authResult) throws IOException, ServletException {
		System.out.println("successfulAuthentication 실행됨: 인증이 완료되었다는 뜻");
		PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
		//RSA방식이 아닌 Hash 암호방식
		String jwtToken = JWT.create()
			.withSubject("cos 토큰")
			.withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 10))) //1000이 1초 -> 10분임
			.withClaim("id", principalDetails.getUser().getId())
			.withClaim("username", principalDetails.getUser().getUsername())
			.sign(Algorithm.HMAC512("cos"));
		response.addHeader("Authorization", "Bearer " + jwtToken);
	}
}
