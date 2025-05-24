package de.zeroco.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    // IMPORTANT: Keep this secret secure! In a real app, load from properties or environment variables.
    // This key is HS256, so it needs to be at least 256 bits (32 bytes)
    private static final SecretKey JWT_SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long JWT_EXPIRATION_MS = 1000 * 60 * 60 * 10; // 10 hours

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                   .verifyWith(JWT_SECRET_KEY)
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Add roles to claims
        String roles = userDetails.getAuthorities().stream()
                           .map(GrantedAuthority::getAuthority)
                           .collect(Collectors.joining(","));
        claims.put("roles", roles);

        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                   .claims(claims)
                   .subject(subject)
                   .issuedAt(new Date(System.currentTimeMillis()))
                   .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                   .signWith(JWT_SECRET_KEY, SignatureAlgorithm.HS256)
                   .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
