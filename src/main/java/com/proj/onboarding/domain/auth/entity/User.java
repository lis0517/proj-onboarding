package com.proj.onboarding.domain.auth.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.proj.onboarding.domain.auth.dto.SignupResponseDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(length = 50, nullable = false)
	private String nickname;

	@Column(nullable = false)
	private String password;

	@ManyToMany
	@JoinTable(name = "user_role_mapping", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private final Set<UserRole> roles = new HashSet<>();

	@Builder
	public User(String username, String nickname, String password) {
		this.username = username;
		this.nickname = nickname;
		this.password = password;
	}

	public void addRole(UserRole role) {
		this.roles.add(role);
		role.getUsers().add(this);
	}

	public void removeRole(UserRole role) {
		this.roles.remove(role);
		role.getUsers().remove(this);
	}

	public SignupResponseDto toDto() {

		return SignupResponseDto.builder()
			.username(username)
			.nickname(nickname)
			.authorities(this.getRoles()
				.stream()
				.map(role -> new SignupResponseDto.AuthorityDto(role.getName()))
				.collect(Collectors.toList()))
			.build();
	}
}
