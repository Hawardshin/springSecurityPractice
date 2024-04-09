package com.cos.jwt.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// @CrossOrigin // 이 어노테이션을 사용하면 모든 요청에 대해 CORS를 허용하게 될 수 있는데 이건 spring security의 인증이 필요한 모든 요청이 거부되기 때문에 사용하지 않는다.
// 즉 인증이 필요 없는 요청만 보이게 됩니다.
@RestController
public class RestApiController {
	@GetMapping("/home")
	public String home() {
		return "<h1>home</h1>";
	}
}
