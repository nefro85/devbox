package io.s7i.webauthn;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class Auth {
    ObjectId id;
    Details authenticator;

    String created;
    String update;
}
