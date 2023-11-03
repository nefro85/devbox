package io.s7i.vertx;

import static java.util.Objects.requireNonNull;

public enum Configuration {
    APP_NAME, ORIGIN, SHOW_ACTIVITY, USE_SSL, CERT_PATH, CERTSTORE_SECRET, MONGODB_URI, AUTH_ATTACHMENT,
    WEB_ROOT;

    public String get() {
        return requireNonNull(System.getenv(this.name()), "Missing Env:" + this.name());
    }
}
