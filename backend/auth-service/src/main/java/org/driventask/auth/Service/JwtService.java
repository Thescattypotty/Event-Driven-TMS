package org.driventask.auth.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.driventask.auth.Enum.ERole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private Key key;

    @PostConstruct
    public void initKey(){
        this.key = Keys.hmacShaKeyFor(secret.getBytes()); 
    }

    public String generateToken(String email , Set<ERole> roles , String tokenType){
        Map<String , Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("roles",
            roles != null 
                ? roles.stream().map(Enum::name).collect(Collectors.toList()) 
                : null
            );

        Long expMillis = "ACCESS".equalsIgnoreCase(tokenType)
            ? expiration * 1000
            : expiration * 1000 * 5;

        return Jwts
            .builder()
            .setClaims(claims)
            .setSubject(email)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expMillis))
            .signWith(key)
            .compact();
    }
}
