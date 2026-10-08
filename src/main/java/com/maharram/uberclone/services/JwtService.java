package com.maharram.uberclone.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
  public static final String SECRET =
      "5367566859703373367639792F423F452848284D6251655468576D5A71347437";

  public String generateToken(String email) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, email);
  }

  public String createToken(Map<String, Object> claims, String email) {
    return Jwts.builder().claims(claims)
        .subject(email)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
        .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
  }

  private Key getSignKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET);
    return Keys.hmacShaKeyFor(keyBytes);
  }
  public String ExtractUserName(String token){
    return extractClaim(token,Claims::getSubject);
  }
  public Date ExtractExpiration(String token){
    return extractClaim(token,Claims::getExpiration);
  }
  public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public Claims extractAllClaims(String token){
    return Jwts.parser().verifyWith((SecretKey) getSignKey()).build().parseSignedClaims(token).getPayload();
  }
  private Boolean isTokenExpired(String token){
    return ExtractExpiration(token).before(new Date());
  }
  public Boolean isValidToken(String token, UserDetails userDetails){
    final String username = ExtractUserName(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }
}
