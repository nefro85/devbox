package io.s7i.webauthn;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class Utils {

    public static List<String> asList(String csv) {
        if (csv == null) {
            return List.of();
        }
        return Arrays.stream(csv.split("\\,"))
              .filter(Predicate.not(String::isEmpty))
              .map(String::trim)
              .collect(Collectors.toList());
    }
}
