package security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private Key key;

    @PostConstruct
    public void init() {
        byte[] secretBytes = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(secretBytes);
    }

    @Value("${jwt.secret}")
    String secret;

    public String generateToken(String email, String role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);

        Date now = new Date();
        long validityInMilliseconds = 1000 * 60 * 60 * 24; // 24 horas
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }


}
