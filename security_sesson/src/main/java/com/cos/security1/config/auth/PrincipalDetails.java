package com.cos.security1.config.auth;


// 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
// 로그인을 진행이 완료가 되면 시큐리티 session을 만들어준다. (Security ContextHolder) 여기에 세션을 저장합니다.
// 오브젝트 => Authentication 타입 객체 여야만 합니다.
// Authentication 안에 User 정보가 있어야 됩니다.
// User 오브젝트 타입 => UserDetails 타입 객체 여야만 합니다.

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.cos.security1.model.User;

import lombok.Data;
// Security Session => Authentication => UserDetails(PrincipalDetails)
@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

	private User user; // 콤포지션
	private Map<String, Object> attributes;

	//일반 로그인 할 때 사용하는 생성자
	public PrincipalDetails(User user) {
		this.user = user;
	}

	// OAuth 로그인 할 때 사용하는 생성자
	public PrincipalDetails(User user, Map<String,Object> attributes) {
		this.attributes = attributes;
		this.user = user;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	//해당 유저의 권한을 리턴하는 곳
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collect = new ArrayList<>();
		collect.add(new GrantedAuthority() {
			@Override
			public String getAuthority() {
				return user.getRole();
			}
		});
		return collect;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() { //계정이 만료되었는지 안되었는지 물어봄
		return true;
	}

	@Override
	public boolean isAccountNonLocked() { //계정 잠겼는지 안잠겼는지 물어봄
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() { //비밀번호 너무 오래 사용한 건 아닌지 물어봄
		return true;
	}

	@Override
	public boolean isEnabled() { //계정 활성화 되어있는지 물어봄
		//우리 사이트 1년동안 회원이 로그인을 안하면 휴먼 계정으로 하기로 함.
		//user.getLoginDate() 를 하고 현재시간 - loginDate > 1년 이면 return false; 할 수도 있다.
		return true;
	}

	//아건 안 중요해서 null로 둠
	@Override
	public String getName() {
		return null;
	}
}
