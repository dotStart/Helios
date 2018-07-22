/*
 * Copyright 2018 Hex <hex@hex.lc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.dotstart.helios.api.event;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * Represents a localized event bus.
 */
public class EventGroup {
    private Map<Class<? extends Event>, CopyOnWriteArrayList<Consumer<Event>>> handlers;
    private Cache<Method, CallSite> callSiteCache;

    @Inject
    private Logger logger;

    @Inject
    private Injector injector;

    {
        handlers = new HashMap<>();
        callSiteCache = CacheBuilder.newBuilder().weakKeys().weakValues().build();
    }

    @SuppressWarnings({"unchecked", "SuspiciousMethodCalls"})
    public <T extends Event> boolean subscribe(Consumer<T> handler) {
        Class<?> eventType = getConsumerType(handler);
        logger.debug("Subscribing event consumer {} to type {}", handler.getClass().getName(), eventType);
        return handlers.get(eventType).add((Consumer<Event>) handler);
    }

    // TODO test this method, every time I write it for a different project it breaks.
    @SuppressWarnings("unchecked")
    public void subscribe (Class<?> clazz) {
        MethodHandles.Lookup lookup;

        try {
            lookup = MethodHandles.privateLookupIn(clazz, MethodHandles.lookup());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Arrays.stream(clazz.getDeclaredMethods()).filter(m -> m.isAnnotationPresent(Subscribe.class)).forEach(method -> {
            MethodHandle handle;
            try {
                 handle = lookup.unreflect(method);
                CallSite callSite = callSiteCache.get(method, () -> LambdaMetafactory.metafactory(lookup,
                        "accept",
                        MethodType.methodType(Consumer.class, clazz),
                        handle.type().dropParameterTypes(0, 1).changeParameterType(0, Object.class),
                        handle,
                        handle.type().dropParameterTypes(0, 1)));
                logger.debug("Completed LambdaMetafactory invocation for {}", method.getName());
                subscribe((Consumer) callSite.getTarget().invoke(injector.getInstance(clazz)));
            } catch (IllegalAccessException | ExecutionException e) {
                throw new RuntimeException(e);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    public <T extends Event> void post(T event) {
        logger.debug("event-group post {}", event.getClass().getName()); // TODO maybe serialize events here?
        handlers.get(event.getClass()).forEach((c -> c.accept(event)));
    }

    public <T extends Event & Cancellable> boolean postCancellable(T event) {
        post(event);
        return !event.isCancelled();
    }

    private Class<?> getConsumerType(Consumer<?> consumer) {
        return consumer.getClass().getTypeParameters()[0].getGenericDeclaration();
    }
}
