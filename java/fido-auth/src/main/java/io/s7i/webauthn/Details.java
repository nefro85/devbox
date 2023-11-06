package io.s7i.webauthn;

import io.vertx.ext.auth.webauthn.Authenticator;
import lombok.Data;

@Data
public class Details {

    static Details from(Authenticator authenticator) {
        var details = new Details();
        details.counter = authenticator.getCounter();
        details.credID = authenticator.getCredID();
        details.flags = authenticator.getFlags();
        details.fmt = authenticator.getFmt();
        details.publicKey = authenticator.getPublicKey();
        details.type = authenticator.getType();
        details.userName = authenticator.getUserName();
        return details;
    }

    Long counter;
    String credID;
    Integer flags;
    String fmt;
    String publicKey;
    String type;
    String userName;

    public Authenticator toAuthenticator() {
        var a = new Authenticator();

        a.setCounter(counter);
        a.setCredID(credID);
        a.setFlags(flags);
        a.setFmt(fmt);
        a.setPublicKey(publicKey);
        a.setType(type);
        a.setUserName(userName);

        return a;
    }
}
