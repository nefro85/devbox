package io.s7i.token;

import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class UserTokenGenerator {

    public record Token(String jwt) {

        public JsonObject asJson() {
            var jsonObject = new JsonObject();
            jsonObject.put("id", UUID.randomUUID().toString());
            jsonObject.put("token", jwt);
            return jsonObject;
        }
    }

    private static final JwtToken jwtToken = new JwtToken();

    public static Token generate(String usrName, List<String> roles) {

        var claims = Map.of(
              "userName", usrName,
              "roles", String.join(",", roles)
        );
        return new Token(jwtToken.build(claims));
    }
}
