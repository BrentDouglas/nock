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
package io.machinecode.chainlink.rt.glassfish;

import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.core.Environment;
import io.machinecode.chainlink.core.configuration.ClassLoaderFactoryImpl;
import io.machinecode.chainlink.core.configuration.DeploymentModelImpl;
import io.machinecode.chainlink.core.configuration.JobOperatorModelImpl;
import io.machinecode.chainlink.core.configuration.SubSystemModelImpl;
import io.machinecode.chainlink.core.execution.EventedExecutorFactory;
import io.machinecode.chainlink.core.execution.ThreadFactoryLookup;
import io.machinecode.chainlink.core.management.LazyJobOperator;
import io.machinecode.chainlink.core.management.jmx.PlatformMBeanServerFactory;
import io.machinecode.chainlink.core.marshalling.JdkMarshallingFactory;
import io.machinecode.chainlink.core.registry.LocalRegistryFactory;
import io.machinecode.chainlink.core.repository.memory.MemoryRepositoryFactory;
import io.machinecode.chainlink.core.schema.Configure;
import io.machinecode.chainlink.core.schema.SubSystemSchema;
import io.machinecode.chainlink.core.configuration.Model;
import io.machinecode.chainlink.core.transaction.JndiTransactionManagerFactory;
import io.machinecode.chainlink.core.transport.LocalTransportFactory;
import io.machinecode.chainlink.core.util.Tccl;
import io.machinecode.chainlink.rt.glassfish.schema.GlassfishSubSystem;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import org.glassfish.internal.data.ApplicationInfo;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GlassfishEnvironment implements Environment, AutoCloseable {

    private final ConcurrentMap<String, App> operators = new ConcurrentHashMap<>();
    private SubSystemModelImpl model;
    private final ThreadFactoryLookup threadFactory;
    private final Object lock = new Object();
    private final GlassfishSubSystem subSystem;

    public GlassfishEnvironment(final ThreadFactoryLookup threadFactory, final GlassfishSubSystem subSystem) {
        this.threadFactory = threadFactory;
        this.subSystem = subSystem;
    }

    @Override
    public ExtendedJobOperator getJobOperator(final String name) throws NoConfigurationWithIdException {
        final ClassLoader tccl = Tccl.get();
        for (final App app : operators.values()) {
            if (tccl.equals(app.loader.get())) {
                final LazyJobOperator op = app.ops.get(name);
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
    public void reload() throws Exception {
        synchronized (lock) {
            if (this.model == null) {
                throw new IllegalStateException(); //TODO Message
            }
            final ClassLoader loader = this.model.getClassLoader();
            final SubSystemModelImpl model = new SubSystemModelImpl(loader);
            Model.configureSubSystem(model, subSystem, loader);
            //TODO This now need to reload operators
            this.model = model;
        }
    }

    public Map<String, LazyJobOperator> getJobOperators() {
        final ClassLoader tccl = Tccl.get();
        for (final App app : operators.values()) {
            if (tccl.equals(app.loader.get())) {
                return Collections.unmodifiableMap(app.ops);
            }
        }
        return Collections.emptyMap();
    }

    public void addSubsystem(final ClassLoader loader, final GlassfishSubSystem subSystem) throws Exception {
        final SubSystemModelImpl model;
        synchronized (lock) {
            model = this.model = new SubSystemModelImpl(loader);
        }
        Model.configureSubSystem(model, subSystem, loader);
    }

    public void addApplication(final ApplicationInfo info) throws Exception {
        final SubSystemModelImpl model;
        synchronized (lock) {
            if (this.model == null) {
                throw new IllegalStateException(); //TODO Message
            }
            model = this.model;
        }
        App app = this.operators.get(info.getName());
        final ClassLoader loader = info.getAppClassLoader();
        if (app == null) {
            app = new App(loader);
            this.operators.put(info.getName(), app);
        }
        final DeploymentModelImpl deployment = model.findDeployment(info.getName()).copy(loader);

        deployment.loadChainlinkXml();

        boolean haveDefault = false;
        for (final Map.Entry<String, JobOperatorModelImpl> entry : deployment.getJobOperators().entrySet()) {
            final JobOperatorModelImpl jobOperatorModel = entry.getValue();
            configureJobOperator(jobOperatorModel, loader, threadFactory);
            if (Constants.DEFAULT.equals(entry.getKey())) {
                haveDefault = true;
            }
            final LazyJobOperator op = jobOperatorModel.createLazyJobOperator();
            app.ops.put(
                    entry.getKey(),
                    op
            );
        }
        if (!haveDefault) {
            final JobOperatorModelImpl defaultModel = deployment.getJobOperator(Constants.DEFAULT);
            configureJobOperator(defaultModel, loader, threadFactory);
            final LazyJobOperator op = defaultModel.createLazyJobOperator();
            app.ops.put(
                    Constants.DEFAULT,
                    op
            );
        }
    }

    public void removeApplication(final ApplicationInfo info) throws Exception {
        final App app = this.operators.remove(
                info.getName()
        );
        Exception exception = null;
        for (final LazyJobOperator op : app.ops.values()) {
            try {
                op.close();
            } catch (Exception e) {
                if (exception == null) {
                    exception = e;
                } else {
                    exception.addSuppressed(e);
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    @Override
    public void close() throws Exception {
        // TODO
    }

    private static void configureJobOperator(final JobOperatorModel model, final ClassLoader loader, final ThreadFactoryLookup threadFactory) throws Exception {
        model.getClassLoader().setDefaultFactory(new ClassLoaderFactoryImpl(loader));
        model.getTransactionManager().setDefaultFactory(new JndiTransactionManagerFactory("java:appserver/TransactionManager"));
        model.getRepository().setDefaultFactory(new MemoryRepositoryFactory());
        model.getMarshalling().setDefaultFactory(new JdkMarshallingFactory());
        model.getMBeanServer().setDefaultFactory(new PlatformMBeanServerFactory());
        model.getTransport().setDefaultFactory(new LocalTransportFactory());
        model.getRegistry().setDefaultFactory(new LocalRegistryFactory());
        model.getExecutor().setDefaultFactory(new EventedExecutorFactory(threadFactory));
    }

    private static class App {
        final WeakReference<ClassLoader> loader;
        final ConcurrentMap<String, LazyJobOperator> ops;

        private App(final ClassLoader loader) {
            this.loader = new WeakReference<>(loader);
            this.ops = new ConcurrentHashMap<>();
        }
    }
}
