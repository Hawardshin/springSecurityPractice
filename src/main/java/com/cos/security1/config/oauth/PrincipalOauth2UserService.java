package com.cos.security1.config.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	UserRepository userRepository;

	//loadUser() 라는 함수에서 어떤식으로 로그인을 후처리할지 정의 가능

	// 구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
	@Override
	// userRequest 데이터에는 이미 코드와 엑세트 토큰으로 부터 받은 사용자 정보가 들어있음
	// 함수가 종료되면 @AuthenticationPrincipal 어노테이션이 만들어진다. 즉 서비스가 호출될 때 해당 어노테이션이 호출된다는 것을 기억하자.
	public PrincipalDetails loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		System.out.println(
			"getClientRegistration: " + userRequest.getClientRegistration()); //registrationId로 어떤 OAuth로 로그인 했는지 확인 가능
		//getClientRegistration 에는 우리 서버의 기본 정보들이 들어가있다.
		//클라이언트 아이디, 클라이언트 시크릿, 리다이렉트 uri 등등
		System.out.println("getAccessToken: " + userRequest.getAccessToken().getTokenValue());
		// 구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인 완료 -> 코드 리턴 (OAuth-Client라이브러리가 받아줌) -> 엑세스 토큰 요청 (여기까지가 userRequest 정보)
		// userRequest 정보를 이용해서 -> 회원 프로필을 받아야한다.(loadUser 함수 호출) -> 구글로부터 회원프로필 받아준다.
		// loadUser 함수의 역할은
		System.out.println("getAttributes: " + super.loadUser(userRequest).getAttributes());

		OAuth2User oauth2User = super.loadUser(userRequest);

		// 회원 가입을 강제로 진행해볼 예정
		String Provider = userRequest.getClientRegistration().getClientId();
		String ProviderId = oauth2User.getAttribute("sub");
		String username = Provider + "_" + ProviderId;
		String email = oauth2User.getAttribute("email");
		String password = bCryptPasswordEncoder.encode("겟인데어");
		String role = "ROLE_USER";

		// 이미 가입된 회원인지 확인
		// 가입되지 않은 회원이라면 회원가입 진행
		// 가입된 회원이라면 로그인 진행
		User userEntity = userRepository.findByUsername(username);

		if (userEntity == null){
			userEntity = User.builder()
				.username(username)
				.password(password)
				.email(email)
				.role(role)
				.provider(Provider)
				.providerId(ProviderId)
				.build();
			userRepository.save(userEntity);
		}else {
			System.out.println("이미 가입된 회원입니다.");
		}
		//이게 생성이 되서 Authentication 객체에 들어가게 된다.
		return new PrincipalDetails(userEntity, oauth2User.getAttributes());
	}

}
