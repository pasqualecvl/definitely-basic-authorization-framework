package it.pasqualecavallo.studentsmaterial.authorization_framework.utils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

	@Value("${authframework.hskey}")
	private String hsKey;

	@Value("${authframework.audience}")
	private String audience;

	@Value("${authframework.token-ttl}")
	private long ttl;

	private Key key;

	@PostConstruct
	public void postContruct() {
		this.key = Keys.hmacShaKeyFor(hsKey.getBytes());
	}

	public String getJws(String user, List<String> roles) {
		return Jwts.builder().setSubject(user).setAudience(audience)
				.setExpiration(new Date(System.currentTimeMillis() + ttl)).setId(UUID.randomUUID().toString())
				.setIssuedAt(new Date()).setNotBefore(new Date()).addClaims(new HashMap<>() {
					private static final long serialVersionUID = 1L;
						{
							put(Constants.CLAIM_USER_ROLES, roles);
						}
				}).signWith(key).compact();
	}

	public Jws<Claims> decodeJwt(String jwt) {
		try {
			return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);
		} catch (JwtException ex) {    
			return null;
		}
	}
}