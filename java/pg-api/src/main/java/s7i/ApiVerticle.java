package s7i;

import io.vertx.core.Promise;
import io.vertx.core.http.impl.MimeMapping;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.pgclient.PgBuilder;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.HashMap;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class ApiVerticle extends HttpServerVerticle {

    public static final Logger LOGGER = LoggerFactory.getLogger(ApiVerticle.class);
    private SqlClient client;

    @Override
    public void start(Promise<Void> start) throws Exception {
        var poolOptions = new PoolOptions()
                .setMaxSize(5);

        var bld = PgBuilder
                .client();

        Optional.ofNullable(System.getenv("DB_CONNECTION_URL"))
                .ifPresent(bld::connectingTo);


        client = bld.with(poolOptions)
                .using(getVertx())
                .build();

        super.start(start);
    }

    @Override
    protected Router initRouter() {
        var router = Router.router(getVertx());

        router.get("/messages")
                .produces(MimeMapping.getMimeTypeForExtension("json"))
                .handler(this::query);

        return router;
    }

    void query(RoutingContext ctx) {

        try {
            var params = new HashMap<String, Object>();
            params.put("all_nodes", 1);
            params.put("from_node", 0);
            params.put("any_chann", 1);
            params.put("channel", 0);

            ctx.queryParam("channel").stream().findFirst().ifPresent(chan -> {
                params.put("any_chann", 0);
                params.put("channel", Integer.parseInt(chan));
            });

            SqlTemplate.forQuery(client, Query.Sql.MESSAGES.text())
                    .mapTo(Row::toJson)
                    .execute(params)
                    .onSuccess(rows -> {

                        LOGGER.debug("getting rows");

                        JsonArray arr = StreamSupport.stream(rows.spliterator(), false)
                                .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
                        ctx.end(arr.toBuffer());

                    })
                    .onFailure(h -> {
                        LOGGER.error("failure", h);

                        ctx.response()
                                .setStatusCode(500)
                                .end(h.getMessage());
                    });
        } catch (Exception e) {
            LOGGER.error("oops", e);
        }

    }
}