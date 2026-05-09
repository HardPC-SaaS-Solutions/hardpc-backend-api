package com.hardpc.saas.backendapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Clave de firma (En producción debe ir en variables de entorno)
    // Es "SeguridadAvanzadaHardPC_LlaveMaestraParaTokens_2026" en Base64
    private static final String SECRET_KEY = "U2VndXJpZGFkQXZhbnphZGFIYXJkUENfTGxhdmVNYWVzdHJhUGFyYVRva2Vuc18yMDI2";

    /**
     * Extrae el correo electrónico (subject) que está oculto dentro del token JWT.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Genera un token JWT básico usando solo los datos del usuario.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * La "Fábrica" del token. Genera un JWT inyectando datos adicionales (claims) como el rol,
     * define la fecha de emisión y le establece una expiración de 24 horas.
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                // Expira en 24 horas
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * El "Juez". Valida que el token pertenezca al usuario que intenta hacer la petición
     * y verifica matemáticamente que el token no haya caducado.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Verifica si la fecha actual del servidor ya superó la fecha de expiración del token.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrae específicamente el dato de la fecha de caducidad del token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Método genérico (Lupa) que permite extraer cualquier dato específico (claim)
     * pasando una función de resolución.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Abre el token, verifica que la firma sea auténtica y extrae todo su contenido (Payload).
     * Utiliza la sintaxis moderna de JJWT 0.13.0.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Convierte la clave secreta de texto Base64 a una llave criptográfica real
     * utilizando el algoritmo de alta seguridad HMAC-SHA256.
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}