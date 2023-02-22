package picohttp;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.util.Objects.requireNonNull;

public class Main {

    public static final int EC_BAD_USAGE = 2;

    public static void main(String[] args) throws Exception {
        if(args.length == 0) {
            System.err.printf("Bad arguments\n");
            System.exit(EC_BAD_USAGE);
        }

        var hc = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();

        var rb = HttpRequest.newBuilder()
                .uri(URI.create(requireNonNull( args[0] , "args[0] - uri")));

        if (args.length == 1) {
            rb.GET();

            var rq = rb.build();
            var rsp = hc.send(rq, HttpResponse.BodyHandlers.ofString());

            System.out.printf(
                    "Http request status: %s\n" +
                            "--- DATA BEGIN ---\n" +
                            "%s\n--- DATA END ---\n",
                    String.valueOf(rsp.statusCode()),
                    rsp.body()
            );
        } else {
            System.exit(EC_BAD_USAGE);
        }
    }
}