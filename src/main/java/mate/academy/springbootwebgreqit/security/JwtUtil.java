package mate.academy.springbootwebgreqit.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private static final Logger logger = Logger.getLogger(JwtUtil.class.getName());
    private static final String TOKEN_TYPE_CLAIM = "typ";
    private static final String ACCESS = "access";
    private static final String REFRESH = "refresh";

    private final Key secret;

    @Value("${jwt.access.expiration}")
    private long accessExpirationMs;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpirationMs;

    public JwtUtil(@Value("${jwt.secret}") String secretString) {
        this.secret = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String username) {
        return buildToken(username, ACCESS, accessExpirationMs);
    }

    public String generateRefreshToken(String username) {
        return buildToken(username, REFRESH, refreshExpirationMs);
    }

    private String buildToken(String username, String type, long ttlMs) {
        return Jwts.builder()
                .setSubject(username)
                .claim(TOKEN_TYPE_CLAIM, type)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ttlMs))
                .signWith(secret)
                .compact();
    }

    public boolean isAccessTokenValid(String token) {
        return validateTokenOfType(token, ACCESS);
    }

    public boolean isRefreshTokenValid(String token) {
        return validateTokenOfType(token, REFRESH);
    }

    private boolean validateTokenOfType(String token, String expectedType) {
        try {
            Claims claims = parseClaimsJws(token);
            if (!expectedType.equals(claims.get(TOKEN_TYPE_CLAIM, String.class))) {
                return false;
            }
            boolean expired = claims.getExpiration().before(new Date());
            if (expired) {
                logger.warning("Token is expired");
            }
            return !expired;
        } catch (JwtException | IllegalArgumentException e) {
            logger.severe("Invalid JWT token: " + e.getMessage());
            return false;
        }
    }

    public String getUsername(String token) {
        return getClaim(token, Claims::getSubject);
    }

    private <T> T getClaim(String token, Function<Claims, T> resolver) {
        Claims claims = parseClaimsJws(token);
        return resolver.apply(claims);
    }

    private Claims parseClaimsJws(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
