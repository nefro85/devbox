package io.s7i;

import io.s7i.webauthn.Utils;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UtilsTest {


    @Test
    void testUtil() {

        var list = Utils.asList("one-item");
        Assertions.assertTrue(list.containsAll(List.of("one-item")));

        Assertions.assertEquals(
              List.of("one-item", "second-item"),
              Utils.asList("one-item, second-item")
        );

    }

}