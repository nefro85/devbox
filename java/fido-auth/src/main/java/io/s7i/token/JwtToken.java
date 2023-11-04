package io.s7i.token;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import io.s7i.vertx.Configuration;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.security.KeyStore;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Slf4j
@UtilityClass
public class JwtToken {
    private static Algorithm algorithm;

    static {
        try {
            File path = new File(Configuration.CERT_PATH.get());
            char[] secret = Configuration.CERTSTORE_SECRET.get().toCharArray();

            var jks = KeyStore.getInstance(path, secret);
            String alias = "rsakey";
            var key = jks.getKey(alias, secret);

            if (key instanceof RSAPrivateKey) {
                var rsaPrivateKey = (RSAPrivateKey) key;
                var rsaPublicKey = (RSAPublicKey) jks.getCertificate(alias).getPublicKey();


                algorithm = Algorithm.RSA256(rsaPublicKey, rsaPrivateKey);

            } else {
                throw new IllegalStateException("No RSA Key");
            }
        } catch (Exception e) {
            log.error("panic", e);
        }
    }

    public static String build() {
        return build(Map.of());
    }

    public static String build(Map<String, String> opts) {
        return jwt(opts).sign(algorithm);
    }

    private static JWTCreator.Builder jwt(Map<String, String> claims) {
        log.info("claims: {}", claims);

        var now = Instant.now();
        var to = now.plus(24, ChronoUnit.HOURS);

        var bu = JWT.create()
                .withIssuer(Configuration.APP_NAME.get())
                .withIssuedAt(now)
                .withExpiresAt(to);
        claims.forEach(bu::withClaim);
        return bu;
    }

    public static void main(String[] args) {
        System.out.println(JwtToken.build());
    }
}
