package com.proj.onboarding.domain.auth.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.proj.onboarding.domain.auth.dto.SignupResponseDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

	@Column
	private String refreshToken;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private final Set<UserRoleMapping> roleMappings = new HashSet<>();

	@Builder
	public User(String username, String nickname, String password) {
		this.username = username;
		this.nickname = nickname;
		this.password = password;
	}

	public void addRole(UserRole role) {
		UserRoleMapping mapping = UserRoleMapping.builder().user(this).role(role).build();
		this.roleMappings.add(mapping);
	}

	public void removeRole(UserRole role) {
		this.roleMappings.removeIf(mapping -> mapping.getRole().equals(role));
	}

	public Set<UserRole> getRoles() {
		return this.roleMappings.stream().map(UserRoleMapping::getRole).collect(Collectors.toSet());
	}

	public void updateRefreshToken(String refreshToken){
		this.refreshToken = refreshToken;
	}

	public SignupResponseDto toDto() {

		return SignupResponseDto.builder()
			.username(username)
			.nickname(nickname)
			.authorities(
				this.getRoles().stream().map(role -> new SignupResponseDto.AuthorityDto(role.getName())).toList())
			.build();
	}
}
