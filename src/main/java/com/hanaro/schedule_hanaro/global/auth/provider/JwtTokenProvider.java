package com.hanaro.schedule_hanaro.global.auth.provider;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hanaro.schedule_hanaro.global.auth.dto.response.JwtTokenDto;
import com.hanaro.schedule_hanaro.global.domain.enums.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider implements InitializingBean {
	@Value("${jwt.secret}")
	private String secret;
	@Value("${jwt.accessExpiration}")
	private Integer accessExpiration;
	@Value("${jwt.refreshExpiration}")
	private Integer refreshExpiration;
	private Key key;

	@Override
	public void afterPropertiesSet() throws Exception {
		byte[] decoded = Base64.getDecoder().decode(secret);
		this.key = Keys.hmacShaKeyFor(decoded);
	}

	public String getUsernameFromToken(String token) {
		Claims claims = validateToken(token);
		return claims.getId();
	}

	public Claims validateToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}

	public String generateToken(String id, Role role, Integer expiration) {
		Claims claims = Jwts.claims().setId(id);
		if (role != null) {
			claims.put("role", role);
		}
		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + expiration))
			.signWith(key)
			.compact();
	}

	public JwtTokenDto generateTokens(String id, Role role) {
		return JwtTokenDto.of(
			generateToken(id, role, accessExpiration),
			generateToken(id, role, refreshExpiration)
		);
	}

}
