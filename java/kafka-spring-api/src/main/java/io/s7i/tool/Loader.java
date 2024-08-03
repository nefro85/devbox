package io.s7i.tool;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Component
public class Loader {
    @Value("${app.kafka.propFile}")
    String propFile;

    @SneakyThrows
    public Map<String, Object> kafkaConfig() {
        var map = new HashMap<String, Object>();
        Files.readAllLines(Path.of(propFile)).forEach(l -> {
            String[] res = l.split("=");
            map.put(res[0], res[1]);
        });
        return map;
    }
}
