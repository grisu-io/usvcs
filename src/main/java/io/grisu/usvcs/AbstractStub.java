package io.grisu.usvcs;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import io.grisu.core.exceptions.GrisuException;
import io.grisu.pojo.utils.ReflectionUtils;
import io.grisu.usvcs.annotations.MicroService;
import io.grisu.usvcs.annotations.NanoService;

public abstract class AbstractStub {

    private Client client;
    private final Class uServiceHandler;
    private final String serviceQueue;
    private final Map<Integer, String> nServicesHandlers;

    public AbstractStub(Client client) {
        this.client = client;

        uServiceHandler = Stream.of(this.getClass().getInterfaces())
            .filter(i -> i.getAnnotation(MicroService.class) != null)
            .findFirst()
            .orElseThrow(() ->
                new RuntimeException("Service not annotated with @MicroService (" + this.getClass() + ")")
            );

        serviceQueue = ((MicroService) uServiceHandler.getAnnotation(MicroService.class)).serviceQueue();

        nServicesHandlers = new HashMap<>();
        Stream.of(uServiceHandler.getMethods())
            .forEach(m -> {
                final NanoService nanoServiceAnnotation = m.getAnnotation(NanoService.class);
                if (nanoServiceAnnotation != null) {
                    nServicesHandlers.put(ReflectionUtils.computeSignatureHash(m), nanoServiceAnnotation.name());
                }
            });
    }

    protected <R> CompletableFuture<R> call(Method invokingMethod, Object... params) {
        try {
            return (CompletableFuture<R>) client.send(serviceQueue,
                nServicesHandlers.get(ReflectionUtils.computeSignatureHash(invokingMethod)),
                invokingMethod.getGenericReturnType(),
                params);
        } catch (Exception e) {
            throw new GrisuException(e);
        }
    }

}
