package io.grisu.usvcs;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

public interface Client {

    <R> CompletableFuture<R> send(String uServiceQueue, String nServiceIdentifier, Type returnType, Object... params);

}