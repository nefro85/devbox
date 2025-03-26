# Web Auth Service

Register response

```json
{
  "rp": {
    "name": "Web Auth Service"
  },
  "user": {
    "id": "CJkPjugNT1C5Xp66sxemtA",
    "name": "xxxx@eee.com",
    "displayName": ""
  },
  "challenge": "s0AdEVLDCyY_wkYhtH7IUp32mU5d895uE12xtjwscYzBCkhuMbfNIdClIvOMJ-4U6y6TRRnVDF-7fgCvYx-O4w",
  "pubKeyCredParams": [
    {
      "alg": -7,
      "type": "public-key"
    },
    {
      "alg": -257,
      "type": "public-key"
    }
  ],
  "authenticatorSelection": {
    "requireResidentKeóy": false,
    "userVerification": "discouraged"
  },
  "timeout": 60000,
  "attestation": "none",
  "extensions": {
    "txAuthSimple": ""
  }
}
```

### Misc | Commands
```bash
docker build -t s7i/fido:lab .
```

```bash
docker run -d -p 8443:8443 -p 5005:5005 --network dev-network --name fido --rm s7i/fido
```

```bash
docker run -d -p 8443:8443 -e ORIGIN=http://localhost:3000 -e REPO_TYPE=mongodb -e APP_FLAGS= -p 5005:5005 --network dev-network --name fido --rm s7i/fido
```

## Links
 - https://w3c.github.io/webauthn/
 - https://github.com/eclipse-vertx/vertx-auth/blob/master/vertx-auth-webauthn/src/main/js/vertx-auth-webauthn.js
 - https://developer.mozilla.org/en-US/docs/Web/API/Web_Authentication_API
 