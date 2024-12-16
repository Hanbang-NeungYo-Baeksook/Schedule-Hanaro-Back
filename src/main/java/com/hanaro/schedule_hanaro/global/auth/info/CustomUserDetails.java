// package com.hanaro.schedule_hanaro.global.auth.info;
//
// import java.util.Collection;
// import java.util.Collections;
// import java.util.List;
//
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.UserDetails;
//
// import com.hanaro.schedule_hanaro.global.domain.enums.Role;
//
// import lombok.Builder;
// import lombok.Getter;
// import lombok.RequiredArgsConstructor;
//
// @Getter
// @Builder
// @RequiredArgsConstructor
// public class CustomUserDetails implements UserDetails {
//
// 	private final Long userId;
// 	private final String username;
// 	private final String password;
// 	private final Role role;
// 	private final Collection<? extends GrantedAuthority> authorities;
//
// 	public static CustomUserDetails of(final Long userId, final String username, final String password,
// 		final Role role) {
// 		return CustomUserDetails.builder()
// 			.userId(userId)
// 			.username(username)
// 			.password(password)
// 			.role(role)
// 			.authorities(Collections.singleton(new SimpleGrantedAuthority(role.getRole())))
// 			.build();
// 	}
//
// 	@Override
// 	public Collection<? extends GrantedAuthority> getAuthorities() {
// 		return List.of();
// 	}
//
// 	@Override
// 	public String getPassword() {
// 		return this.password;
// 	}
//
// 	@Override
// 	public String getUsername() {
// 		return this.username;
// 	}
//
// 	@Override
// 	public boolean isAccountNonExpired() {
// 		return UserDetails.super.isAccountNonExpired();
// 	}
//
// 	@Override
// 	public boolean isAccountNonLocked() {
// 		return UserDetails.super.isAccountNonLocked();
// 	}
//
// 	@Override
// 	public boolean isCredentialsNonExpired() {
// 		return UserDetails.super.isCredentialsNonExpired();
// 	}
//
// 	@Override
// 	public boolean isEnabled() {
// 		return UserDetails.super.isEnabled();
// 	}
// }
