package io.s7i.token;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.s7i.vertx.Configuration;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.security.KeyStore;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Slf4j
public final class JwtToken {

    public static JwtToken getInstance() {
        return Holder.instance;
    }

    private static class Holder {

        final static JwtToken instance = new JwtToken();
    }

    private final Algorithm algorithm;

    private JwtToken() {
        try {
            File path = new File(Configuration.JWT_CERTSTORE_PATH.get());
            char[] secret = Configuration.JWT_CERTSTORE_SECRET.get().toCharArray();

            var jks = KeyStore.getInstance(path, secret);
            var alias = Configuration.JWT_CERT_ALIAS.get();
            var key = jks.getKey(alias, secret);

            if (key instanceof RSAPrivateKey rsaPrivateKey) {
                var rsaPublicKey = (RSAPublicKey) jks.getCertificate(alias).getPublicKey();

                algorithm = Algorithm.RSA256(rsaPublicKey, rsaPrivateKey);
                log.info("JWT Algorithm: {}", algorithm.getName());
            } else {
                throw new IllegalStateException("[JWT] No RSA Key");
            }
        } catch (Exception e) {
            throw new IllegalStateException("[JWT] Cannot instantiate JwtToken functionality", e);
        }
    }

    public String build() {
        return build(Map.of());
    }

    public String build(Map<String, String> opts) {
        return jwt(opts).sign(algorithm);
    }

    private JWTCreator.Builder jwt(Map<String, String> claims) {
        log.debug("claims: {}", claims);

        var now = Instant.now();
        var to = now.plus(Integer.parseInt(Configuration.JWT_TTL_HOURS.get()), ChronoUnit.HOURS);

        var bu = JWT.create()
              .withIssuer(Configuration.APP_NAME.get())
              .withIssuedAt(now)
              .withExpiresAt(to);
        claims.forEach(bu::withClaim);
        return bu;
    }

    public Optional<DecodedJWT> decode(String jwtToken) {
        if (jwtToken == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(JWT.require(algorithm)
                  .withIssuer(Configuration.APP_NAME.get())
                  .build()
                  .verify(jwtToken));
        } catch (JWTVerificationException vex) {
            log.warn("Invalid JWT {}", jwtToken, vex);
        }
        return Optional.empty();
    }

    public static void main(String[] args) {
        System.out.println(JwtToken.getInstance().build());
    }
}
