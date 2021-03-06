/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.rt.wildfly;

import io.machinecode.chainlink.core.Environment;
import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.core.schema.Configure;
import io.machinecode.chainlink.core.schema.SubSystemSchema;
import io.machinecode.chainlink.core.util.Tccl;
import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import org.jboss.msc.service.ServiceName;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * TODO
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class WildFlyEnvironment implements Environment {

    private final ConcurrentMap<String, App> operators = new ConcurrentHashMap<>();

    @Override
    public ExtendedJobOperator getJobOperator(final String name) throws NoConfigurationWithIdException {
        final ClassLoader tccl = Tccl.get();
        for (final App app : operators.values()) {
            if (tccl.equals(app.loader.get())) {
                final JobOperatorImpl op = app.ops.get(name);
                if (op == null) {
                    throw new NoConfigurationWithIdException("No configuration for id: " + name); //TODO Message
                }
                return op;
            }
        }
        throw new NoConfigurationWithIdException("Chainlink not configured for TCCL: " + tccl); //TODO Message
    }

    @Override
    public SubSystemSchema<?,?,?> getConfiguration() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public SubSystemSchema<?,?,?> setConfiguration(final Configure configure) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void reload() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Map<String, JobOperatorImpl> getJobOperators() {
        final ClassLoader tccl = Tccl.get();
        for (final App app : operators.values()) {
            if (tccl.equals(app.loader.get())) {
                return Collections.unmodifiableMap(app.ops);
            }
        }
        return Collections.emptyMap();
    }

    public void addOperator(final ServiceName module, final String name, final ClassLoader loader, final JobOperatorImpl operator) throws Exception {
        final String cn = module.getCanonicalName();
        App app = this.operators.get(cn);
        if (app == null) {
            app = new App(loader);
            this.operators.put(cn, app);
        }
        app.ops.put(name, operator);
    }

    public void removeOperator(final ServiceName module, final String name) throws Exception {
        final String cn = module.getCanonicalName();
        final App app = this.operators.get(cn);
        Exception exception = null;
        for (final Map.Entry<String, JobOperatorImpl> entry : app.ops.entrySet()) {
            if (name.equals(entry.getKey())) {
                try {
                    entry.getValue().close();
                } catch (Exception e) {
                    if (exception == null) {
                        exception = e;
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    private static class App {
        final WeakReference<ClassLoader> loader;
        final ConcurrentMap<String, JobOperatorImpl> ops;

        private App(final ClassLoader loader) {
            this.loader = new WeakReference<>(loader);
            this.ops = new ConcurrentHashMap<>();
        }
    }
}
