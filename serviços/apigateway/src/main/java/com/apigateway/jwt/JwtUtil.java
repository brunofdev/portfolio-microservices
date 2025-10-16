package com.apigateway.jwt;

import com.apigateway.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    public boolean isTokenValid(String token) {
        try {
            // A chamada abaixo já valida a assinatura. Se for inválida, lança uma exceção.
            extractAllClaims(token);
            // Se a assinatura for válida, agora verificamos se o token não está expirado.
            return !isTokenExpired(token); // <-- CORREÇÃO APLICADA AQUI
        } catch (Exception e) {
            // Se qualquer exceção ocorrer (assinatura inválida, malformado, etc.), o token é inválido.
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public UserRole extractUserRole(String token) {
        String roleStr = extractClaim(token, claims -> claims.get("role", String.class));
        if(roleStr == null){
            return UserRole.USER;
        }
        return UserRole.valueOf(roleStr);
    }
}