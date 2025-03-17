package com.nexus.nexus.MyPackage.Configuration;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtRequestUtil {
    private final String secret_key = "5367566859703373367639792F423F452848284D6251655468576D5A71347437";

    public String generateToken(String value) {
        Date expireAt = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, value, expireAt);
    }

    private String createToken(Map<String, Object> claims, String subject, Date expireAt) {
        return Jwts.builder().setSubject(subject)
                .setClaims(claims).setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(expireAt)
                .signWith(getKey())
                .compact();
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret_key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claim = extractAllClaims(token);
        return claimsResolver.apply(claim);

    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getKey())
                .parseClaimsJws(token) // Note the 'Jws' here for signed tokens
                .getBody();
    }

    public boolean validateToken(String token, UserDetails userdetails) {
        final String username = extractUsername(token);
        return (username.equals(userdetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
