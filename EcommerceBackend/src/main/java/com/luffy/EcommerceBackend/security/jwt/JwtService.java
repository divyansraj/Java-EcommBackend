package com.luffy.EcommerceBackend.security.jwt;

import com.luffy.EcommerceBackend.security.service.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.aspectj.lang.NoAspectBoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
public class JwtService {


    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME;

    //random secret key
//    public JwtService(){
//        secretKey = getSecretKey();
//    }
//
//    public String getSecretKey(){
//        try{
//            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
//            SecretKey secretKey = keyGen.generateKey();
//            System.out.println("secret key :" + secretKey.toString());
//            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
//        }
//        catch(NoSuchAlgorithmException e){
//            throw new RuntimeException("Error generating secret key",e);
//        }
//    }
    public String generateToken(UserPrincipal principal){
        Map<String,Object> claims = new HashMap<>();
        return Jwts.builder()
                .setSubject(principal.getUsername())//Sets the JWT subject (typically the user’s email or username).
                .claim("role", principal.getRole())//Adds a custom claim for the user’s role (e.g., "USER", "ADMIN").
                .claim("name",principal.getName())//Adds a custom claim for the user’s name.
                .setIssuedAt(new Date(System.currentTimeMillis()))//Sets the issuance time (iat) to now.
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))//Sets the expiration time (exp) to 3 hours from now.
                .signWith(getKey(), SignatureAlgorithm.HS256)//Signs the token with the secret key (from getKey()) using HS256.
                .compact();//Generates the final JWT string
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserPrincipal userPrincipal) {
        final String username = extractUsername(token);
        return username.equals(userPrincipal.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }



    private Key getKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
