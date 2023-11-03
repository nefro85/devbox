# Web Auth Service



### Misc | Commands
```bash
docker build -t s7i/fido .
```

```bash
docker run -d -p 8443:8443 -p 5005:5005 --network dev-network --name fido --rm s7i/fido
```

## Links
 - https://w3c.github.io/webauthn/
 - https://github.com/eclipse-vertx/vertx-auth/blob/master/vertx-auth-webauthn/src/main/js/vertx-auth-webauthn.js
 - https://developer.mozilla.org/en-US/docs/Web/API/Web_Authentication_API
 