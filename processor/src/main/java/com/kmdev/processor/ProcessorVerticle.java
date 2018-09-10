package com.kmdev.processor;

import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;

public class ProcessorVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> future) {
        future.complete();
    }

    @Override
    public void stop(Future<Void> future) {
        future.complete();
    }

}
