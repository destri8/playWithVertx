package io.vertx.example.simpleregis;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.core.Vertx;
import rx.Observable;
import rx.Subscriber;

import java.util.List;

/**
 * Created by Destri on 5/20/15.
 */
public class DataAccess {
    private MongoClient mongoClient;

    public DataAccess(Vertx vertx) {

        JsonObject config = new JsonObject()
                .put("connection_string", "mongodb://localhost:27017")
                .put("db_name", "my_DB");

        mongoClient = MongoClient.createShared(vertx, config);

    }

    public void insert(JsonObject jsonObject) {
        mongoClient.insert("users", jsonObject, res -> {

            if (res.succeeded()) {

                String id = res.result();
                System.out.println("Inserted user with id " + id);

            } else {
                res.cause().printStackTrace();
            }

        });

    }

    public Observable<List<JsonObject>> get(String userId) {
        return Observable.create(subscriber -> {

            mongoClient.find("users", new JsonObject().put("_id", userId), listAsyncResult -> {

                subscriber.onNext(listAsyncResult.result());

            });
        });
    }
}
