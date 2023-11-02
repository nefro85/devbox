package io.s7i.vertx;

import io.s7i.webauthn.AuthenticatorRepository;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.webauthn.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.WebAuthnHandler;

public interface AuthnHelper {

    default void initAuthun(Vertx vertx, AuthenticatorRepository repo, Router router) {
        var authenticatorAttachment = AuthenticatorAttachment.of(Configuration.AUTH_ATTACHMENT.get());
        var options = new WebAuthnOptions()
                .setRelyingParty(new RelyingParty()
                        .setName(Configuration.APP_NAME.get()))
                .setAuthenticatorAttachment(authenticatorAttachment)
                .setUserVerification(UserVerification.DISCOURAGED)
                .setAttestation(Attestation.NONE)
                .setRequireResidentKey(false)
                .setChallengeLength(64)
                .addPubKeyCredParam(PublicKeyCredential.ES256)
                .addPubKeyCredParam(PublicKeyCredential.RS256)
                .addTransport(AuthenticatorTransport.USB)
                .addTransport(AuthenticatorTransport.NFC)
                .addTransport(AuthenticatorTransport.BLE)
                .addTransport(AuthenticatorTransport.INTERNAL);

        var webAuthN = WebAuthn.create(vertx, options)
                .authenticatorFetcher(repo::fetcher)
                .authenticatorUpdater(repo::updater);


        var webAuthnHandler = WebAuthnHandler.create(webAuthN)
                .setOrigin(Configuration.ORIGIN.get())
                .setupCallback(router.post("/webauthn/callback"));

        webAuthnHandler.setupCredentialsCreateCallback(router.post("/webauthn/register"));
        webAuthnHandler.setupCredentialsGetCallback(router.post("/webauthn/login"));

        router.route().handler(webAuthnHandler);

    }
}
