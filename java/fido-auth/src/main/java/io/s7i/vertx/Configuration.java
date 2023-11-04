package io.s7i.vertx;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public enum Configuration {

    APP_NAME, ORIGIN, SHOW_ACTIVITY, USE_SSL, CERT_PATH, CERTSTORE_SECRET, MONGODB_URI, AUTH_ATTACHMENT,
    WEB_ROOT,
    WEBAUTHN_CALLBACK, WEBAUTHN_REGISTER, WEBAUTHN_LOGIN;

    public String get() {
        final var self = this;
        return Stream.<Function<String, String>>of(
                        System::getenv,
                        arg -> self.defaults()
                )
                .map(f -> f.apply(self.name()))
                .map(Optional::ofNullable)
                .flatMap(Optional::stream)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing Configuration:" + self.name()));
    }

    String defaults() {
        switch (this) {
            case WEBAUTHN_CALLBACK:
                return AuthnHelper.WEBAUTHN_CALLBACK;
            case WEBAUTHN_REGISTER:
                return AuthnHelper.WEBAUTHN_REGISTER;
            case WEBAUTHN_LOGIN:
                return AuthnHelper.WEBAUTHN_LOGIN;
            default:
                return null;
        }
    }
}
