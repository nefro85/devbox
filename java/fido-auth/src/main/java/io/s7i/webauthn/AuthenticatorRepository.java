package io.s7i.webauthn;

import io.vertx.core.Future;
import io.vertx.ext.auth.webauthn.Authenticator;

import java.util.List;

public interface AuthenticatorRepository {
    Future<List<Authenticator>> fetcher(Authenticator query);

    Future<Void> updater(Authenticator authenticator);
}
