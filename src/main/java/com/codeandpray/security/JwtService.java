package com.codeandpray.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final JwtParser parser;
    private final Clock clock;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs,
            Clock clock
    ) {
        if (expirationMs <= 0) {
            throw new IllegalArgumentException("JWT expiration must be positive");
        }
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.clock = clock;
        this.expirationMs = expirationMs;
        this.parser = Jwts.parser().verifyWith(key)
                .requireIssuer("fsp")
                .clock(() -> Date.from(clock.instant()))
                .build();
    }

    public String generateToken(SecurityUser user) {
        var now = clock.instant();
        return Jwts.builder()
                .issuer("fsp")
                .subject(user.getId().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(key)
                .compact();
    }

    public long extractUserId(String token) {
        Claims claims = parser.parseSignedClaims(token).getPayload();
        if (claims.getExpiration() == null || claims.getIssuedAt() == null
                || !claims.getExpiration().toInstant().isAfter(clock.instant())) {
            throw new JwtException("Missing or invalid token dates");
        }
        try {
            long id = Long.parseLong(claims.getSubject());
            if (id <= 0) {
                throw new NumberFormatException();
            }
            return id;
        } catch (RuntimeException ex) {
            throw new JwtException("Invalid subject", ex);
        }
    }
}