package s7i;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public interface Query {

    enum Sql {
        MESSAGES;

        String text() {
            return switch (this) {
                case MESSAGES -> load("mesh_messages.sql");
                default -> throw new IllegalStateException();
            };
        }
    }

    Logger LOGGER = LoggerFactory.getLogger(Query.class);
    static String load(String name) {
        try (var stream = ApiVerticle.class.getClassLoader().getResourceAsStream(name)) {
            if (stream != null) {
                try {
                    var value = new String(stream.readAllBytes());

                    LOGGER.debug("loaded value: {}", value);
                    return value;
                } catch (IOException e) {
                    LOGGER.error("cannot load sql", e);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        throw new IllegalStateException("missing resource:" + name);
    }
}
