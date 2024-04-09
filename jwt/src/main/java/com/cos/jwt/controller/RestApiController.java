package com.cos.jwt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cos.jwt.model.User;
import com.cos.jwt.repository.UserRepository;

import lombok.RequiredArgsConstructor;

// @CrossOrigin // 이 어노테이션을 사용하면 모든 요청에 대해 CORS를 허용하게 될 수 있는데 이건 spring security의 인증이 필요한 모든 요청이 거부되기 때문에 사용하지 않는다.
// 즉 인증이 필요 없는 요청만 보이게 됩니다.
@RestController
@RequiredArgsConstructor
public class RestApiController {

	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UserRepository userRepository;

	@GetMapping("/home")
	public String home() {
		return "<h1>home</h1>";
	}

	@PostMapping("/token")
	public String token() {
		return "<h1>token</h1>";
	}

	@PostMapping("/join")
	public String join(@RequestBody User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setRoles("ROLE_USER");
		userRepository.save(user);
		return "회원가입 완료";
	}
}
