package com.cos.jwt.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import filter.MyFilter1;
import filter.MyFilter2;

@Configuration
public class FilterConfig {
	@Bean
	public FilterRegistrationBean<MyFilter1> filter1(){
		FilterRegistrationBean<MyFilter1> bean = new FilterRegistrationBean<>(new MyFilter1());
		bean.addUrlPatterns("/*"); //모든 요청에서
		bean.setOrder(1); // 낮은 번호가 필터중에서 가장 먼저 실행됨. 즉 우선순위가 가장 높다.
		return bean;
	}

	@Bean
	public FilterRegistrationBean<MyFilter2> filter2(){
		FilterRegistrationBean<MyFilter2> bean = new FilterRegistrationBean<>(new MyFilter2());
		bean.addUrlPatterns("/*"); //모든 요청에서
		bean.setOrder(0); // 낮은 번호가 필터중에서 가장 먼저 실행됨. 즉 우선순위가 가장 높다.
		return bean;
	}
	// MyFilter2 doFilter
	// MyFilter1 doFilter

}
