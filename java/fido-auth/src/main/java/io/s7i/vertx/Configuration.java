package io.s7i.vertx;

import io.s7i.webauthn.Utils;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Configuration {

    APP_NAME, ORIGIN, SHOW_ACTIVITY, USE_SSL, CERT_PATH, CERTSTORE_SECRET, AUTH_ATTACHMENT,
    WEB_ROOT, CONTEXT_ROOT, SERVER_HOST, SERVER_PORT,
    APP_FLAGS,
    WEBAUTHN_CALLBACK, WEBAUTHN_REGISTER, WEBAUTHN_LOGIN,
    REPO_TYPE, ROCKSDB_PATH, MONGODB_URI,
    JWT_CERTSTORE_PATH,
    JWT_CERTSTORE_SECRET,
    JWT_CERT_ALIAS,
    JWT_TTL_HOURS,
    JWT_COOKIE_DOMAIN,
    REQUIRED_ROLES;

    public String get() {
        final var self = this;
        return find().orElseThrow(() -> new IllegalStateException("Missing Configuration:" + self.name()));
    }

    public Optional<String> find() {
        final var self = this;
        return Stream.<Function<String, String>>of(
                        System::getenv,
                        arg -> self.defaults()
                )
                .map(f -> f.apply(self.name()))
                .map(Optional::ofNullable)
                .flatMap(Optional::stream)
                .findFirst();
    }

    String defaults() {
        return switch (this) {
            case WEBAUTHN_CALLBACK -> AuthnHelper.WEBAUTHN_CALLBACK;
            case WEBAUTHN_REGISTER -> AuthnHelper.WEBAUTHN_REGISTER;
            case WEBAUTHN_LOGIN -> AuthnHelper.WEBAUTHN_LOGIN;
            case SERVER_HOST -> AuthServer.HOST;
            case SERVER_PORT -> AuthServer.PORT;
            case CONTEXT_ROOT -> "/";
            case JWT_TTL_HOURS -> "24";
            default -> null;
        };
    }

    public List<String> list() {
        return Utils.asList(this.get());
    }

    public static boolean devMode() {
        return APP_FLAGS.list().contains("dev");
    }
}
