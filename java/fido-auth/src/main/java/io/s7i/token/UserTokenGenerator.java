package io.s7i.token;

import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;

public class UserTokenGenerator {

    final JwtToken jwtToken = new JwtToken();

    public JsonObject generate(String usrName, List<String> roles) {

        var jsonObject = new JsonObject();
        var claims = Map.of(
                "userName", usrName,
                "roles", String.join(",", roles)
        );
        jsonObject.put("token", jwtToken.build(claims));
        return jsonObject;
    }
}
