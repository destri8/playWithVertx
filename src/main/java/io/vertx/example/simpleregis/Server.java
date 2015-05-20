/*
 * Copyright 2014 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.example.simpleregis;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.example.util.Runner;
import io.vertx.ext.apex.Router;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.ext.apex.handler.BodyHandler;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.core.AbstractVerticle;


import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Server extends AbstractVerticle {

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {
        Runner.runExample(Server.class);
    }

    private Server that = this;

    private DataAccess dataAccess;

    @Override
    public void start() {



        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.get("/user/:userID").handler(that::handleGetUser);
        router.post("/user/:userID").handler(that::handlePostUser);

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);


        dataAccess = new DataAccess(vertx);
    }


    /*
        contoh hit url : GET http://localhost:8080/user/userbaru1
     */
    private void handleGetUser(RoutingContext routingContext) {
        String userId = routingContext.request().getParam("userID");
        HttpServerResponse response = routingContext.response();
        if (userId == null) {
            sendError(400, response);
        } else {
            dataAccess.get(userId).subscribe(results -> {

                if (results == null || results.size() == 0) {
                    sendError(404, response);
                } else {
                    response.putHeader("content-type", "application/json").end(results.get(0).encode());
                }

            });
        }
    }

    /*
        contoh hit url : POST http://localhost:8080/user/userbaru2
        request body :
        {
          "_id": "userbaru2",
          "fullname": "userbaru1",
          "friends": [
            "userbaru1",
            "cacaa"
          ]
        }
     */
    private void handlePostUser(RoutingContext routingContext) {
        String userId = routingContext.request().getParam("userID");
        HttpServerResponse response = routingContext.response();
        if (userId == null) {
            sendError(400, response);
        } else {
            JsonObject user = routingContext.getBodyAsJson();
            if (user == null) {
                sendError(400, response);
            } else {
                dataAccess.insert(user);
                response.end();
            }
        }
    }

    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }

}
