package com.cos.security1.model;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class User {

	@Builder
	public User(int id, String username, String password, String email, String role, String provider, String providerId,
		Timestamp createDate) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.role = role;
		this.provider = provider;
		this.providerId = providerId;
		this.createDate = createDate;
	}


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id; //PK
	private String username;
	private String password;
	private String email;
	private String role; //ROLE_USER, ROLE_ADMIN

	private String provider;
	private String providerId;

	@CreationTimestamp
	private Timestamp createDate;
}
