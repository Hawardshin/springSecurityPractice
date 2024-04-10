package com.cos.jwt.config.jwt;
import java.io.IOException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwt.config.auth.PrincipalDetails;
import com.cos.jwt.model.User;
import com.cos.jwt.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 시큐리티가 filter를 가지고 있는데 그 필터중에 BasicAuthenticationFilter 라는 것이 있다.
// 권한이나 인증이 필요한 특정 주소를 요청했을 때 위 필터를 무조건 타게 되어있다.
// 만약 권한이나 인증이 필요한 주소가 아니라면 이 필터를 안탄다.
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
	private UserRepository userRepository;
	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
		super(authenticationManager);
		this.userRepository = userRepository;
	}

	//인증이나 권한이 필요한 주소요청이 있을 때 해당 필터를 타게된다.
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws
		IOException,
		ServletException {
		// super.doFilterInternal(request, response, chain);
		// 이거 위에 super 지워야만 합니다. 왜냐면 여기서 응답을 한번 하고 뒤에서 한번 더 하기 때문에 오류가 난다.
		System.out.println("인증이나 권한이 필요한 주소 요청이 됨.");
		String jwtHeader = request.getHeader(JwtProperties.HEADER_STRING);
		System.out.println("jwtHeader = " + jwtHeader);
		//JWT 토큰 검증을 해서 정상적인 사용자인지 확인
		// header가 있는지 확인
		if (jwtHeader == null || !jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
			System.out.println("토큰 형식 오류!!");
			chain.doFilter(request, response);
			return;
		}

		//JWT 토큰을 검증해서 정상적인 사용자인지 확인
		String jwtToken = request.getHeader(JwtProperties.HEADER_STRING).replace(JwtProperties.TOKEN_PREFIX, "");
		String username = com.auth0.jwt.JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
			.build()
			.verify(jwtToken)
			.getClaim("username")
			.asString();
		if (username != null) {
			//서명이 정상적으로 됨
			System.out.println("서명이 정상적으로 됨");
			System.out.println("username = " + username);
			User userEntity = userRepository.findByUsername(username);
			PrincipalDetails principalDetails = new PrincipalDetails(userEntity);
			//Authentication 객체를 강제로 만드는 방법.
			//pasword = null 로 임시로 강제 Authentication 객체를 만드는 것
			//즉 로그인을 해서 만들어진게 아니라 서명을 해서 만들어졌기 떄문에 다음과 같은 로직으로 동작한다.
			//JWT 토큰 서명을 통해서 서명이 정상이면 Authentication 객체를 만들어준다.
			Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

			// 강제로 시큐리티의 세션에 접근하여, Authentication 객체를 저장
			SecurityContextHolder.getContext().setAuthentication(authentication);
			System.out.println("인증 완료");
			chain.doFilter(request, response);
		}
	}
}