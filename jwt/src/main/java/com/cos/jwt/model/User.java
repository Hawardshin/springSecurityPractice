package com.cos.jwt.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;

@Entity
@Data
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String username;
	private String password;
	private String roles; // USER, ADMIN

	// getter
	public List<String> getRoleList() {
		if (this.roles.length() > 0) {
			return List.of(this.roles.split(","));
		}
		return new ArrayList<>();
	}

}
