package io.s7i.vertx;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.s7i.webauthn.AuthenticatorRepository;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.webauthn.*;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.WebAuthnHandler;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class AuthnHelper {

    public static final String ROOT = Configuration.CONTEXT_ROOT.get();
    public static final String WEBAUTHN_CALLBACK = ROOT + "webauthn/callback";
    public static final String WEBAUTHN_REGISTER = ROOT + "webauthn/register";
    public static final String WEBAUTHN_LOGIN = ROOT + "webauthn/login";

    public static void initAuthun(Vertx vertx, AuthenticatorRepository repo, Router router) {
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

        log.info("WebAuthnOptions: {}", options);

        var webAuthN = WebAuthn.create(vertx, options)
              .authenticatorFetcher(repo::fetcher)
              .authenticatorUpdater(repo::updater);

        var webAuthnHandler = WebAuthnHandler.create(webAuthN)
              .setupCallback(router.post(Configuration.WEBAUTHN_CALLBACK.get()));

        webAuthnHandler.setOrigin(Configuration.ORIGIN.get());

        Route registerRoute = router.post(Configuration.WEBAUTHN_REGISTER.get());
        if (Configuration.APP_FLAGS.list().contains("no-register")) {
            log.info("Disabling register route");
            registerRoute.disable();
        }
        webAuthnHandler.setupCredentialsCreateCallback(registerRoute);
        webAuthnHandler.setupCredentialsGetCallback(router.post(Configuration.WEBAUTHN_LOGIN.get()));

        router.route()
              .handler(webAuthnHandler)
              .failureHandler(event -> event.response()
                    .setStatusCode(HttpResponseStatus.FORBIDDEN.code())
                    .end());

        router.errorHandler(500, event -> {
            log.debug("Handling 500 of {}", event);
            event.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end();
        });
    }
}
