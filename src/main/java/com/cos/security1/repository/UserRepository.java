package com.cos.security1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.security1.model.User;

//@Repository 없어도 Ioc가 자동으로 됩니다.
public interface UserRepository extends JpaRepository<User, Integer>{

}
