package expense.tracker.service;

import expense.tracker.configuration.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Autowired
    private JwtConfig jwtConfig;

    String secretKey = jwtConfig.getSecretKey();
    long expiration = jwtConfig.getExpiration();

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // setup token
    public String generateToken(String userId, String email){
        return generateToken(userId, email, expiration);
    }

    // generate token
    public String generateToken(String userId, String email, long expirationMillis){
        return Jwts.builder()
                .setSubject(userId)
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // validate token
    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch (ExpiredJwtException exeception){
            System.out.println("Expired JWT token : " + exeception.getMessage());
        }catch (UnsupportedJwtException exception){
            System.out.println("Unsupported JWT token : " + exception.getMessage());
        }catch (MalformedJwtException exception){
            System.out.println("Malformed JWT token : " + exception.getMessage());
        }catch (SecurityException | IllegalArgumentException exception){
            System.out.println("Security exception : " + exception.getMessage());
        }
        return false;
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ambil userId dari setSubject pada method generate token
    public String extractUserId(String token){
        return extractClaims(token, Claims::getSubject);
    }

    public String extractEmail(String token){
        return extractClaims(token, claims -> claims.get("email", String.class));
    }

    public Date extractExpiration(String token){
        return extractClaims(token, Claims::getExpiration);
    }

}
