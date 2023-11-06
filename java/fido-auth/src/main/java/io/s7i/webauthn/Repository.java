package io.s7i.webauthn;

import io.vertx.ext.auth.webauthn.Authenticator;

import java.util.List;

public interface Repository {

    List<Authenticator> fetcher(Authenticator query);

    Void updater(Authenticator authenticator);

    List<String> getUserRoles(String user);
}
