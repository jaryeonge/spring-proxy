package org.example.spring.proxy.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TokenProviderTest {

    private TokenProvider tokenProvider;

    @BeforeEach
    public void setUp() {
        tokenProvider = new TokenProvider();
    }

    @Test
    public void givenValidToken_whenIsValidToken_thenReturnsTrue() throws Exception {
        // Given
        String validToken = Jwts.builder()
                .setSubject("test")
                .setExpiration(new Date(System.currentTimeMillis() + 10000)) // valid for 10 seconds
                .signWith(SignatureAlgorithm.HS256, "ewkfihn32oirj213opjuweoilfn23ofjopqwfj32f32f32wf32qf23rf23f32qfih312iofn32".getBytes())
                .compact();

        // When
        boolean isValid = tokenProvider.isValidToken(validToken);

        // Then
        assertTrue(isValid);
    }

    @Test
    public void givenInvalidToken_whenIsValidToken_thenReturnsFalse() throws Exception {
        // Given
        String invalidToken = "invalid.token.value";

        // When
        boolean isValid = tokenProvider.isValidToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    public void givenExpiredToken_whenIsValidToken_thenReturnsFalse() throws Exception {
        // Given
        String expiredToken = Jwts.builder()
                .setSubject("test")
                .setExpiration(new Date(System.currentTimeMillis() - 10000)) // expired 10 seconds ago
                .signWith(SignatureAlgorithm.HS256, "ewkfihn32oirj213opjuweoilfn23ofjopqwfj32f32f32wf32qf23rf23f32qfih312iofn32".getBytes())
                .compact();

        // When
        boolean isValid = tokenProvider.isValidToken(expiredToken);

        // Then
        assertFalse(isValid);
    }

}