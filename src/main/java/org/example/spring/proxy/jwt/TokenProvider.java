package org.example.spring.proxy.jwt;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenProvider {

    private static final String TEST_SECRET = "ewkfihn32oirj213opjuweoilfn23ofjopqwfj32f32f32wf32qf23rf23f32qfih312iofn32";

    public boolean isValidToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(TEST_SECRET.getBytes()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

}
