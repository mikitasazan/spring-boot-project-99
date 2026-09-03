package hexlet.code.app.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

	private final SecretKey key;
	private final long expirationMs;

	public JwtUtils(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration-ms}") long expirationMs) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes());
		this.expirationMs = expirationMs;
	}

	public String generateToken(String email) {
		var now = new Date();
		var expiry = new Date(now.getTime() + expirationMs);

		return Jwts.builder()
				.subject(email)
				.issuedAt(now)
				.expiration(expiry)
				.signWith(key)
				.compact();
	}

	public String extractEmail(String token) {
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
	}

}
