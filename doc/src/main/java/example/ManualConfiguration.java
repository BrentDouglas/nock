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
package example;

import io.machinecode.chainlink.core.Chainlink;
import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.core.Environment;
import io.machinecode.chainlink.core.configuration.ClassLoaderFactoryImpl;
import io.machinecode.chainlink.core.configuration.DeploymentModelImpl;
import io.machinecode.chainlink.core.configuration.JobOperatorModelImpl;
import io.machinecode.chainlink.core.configuration.SubSystemModelImpl;
import io.machinecode.chainlink.core.execution.EventedExecutorFactory;
import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.core.marshalling.JdkMarshallingFactory;
import io.machinecode.chainlink.core.registry.LocalRegistryFactory;
import io.machinecode.chainlink.core.repository.memory.MemoryRepositoryFactory;
import io.machinecode.chainlink.core.schema.Configure;
import io.machinecode.chainlink.core.schema.SubSystemSchema;
import io.machinecode.chainlink.core.transaction.LocalTransactionManagerFactory;
import io.machinecode.chainlink.core.transport.LocalTransportFactory;
import io.machinecode.chainlink.core.util.Tccl;
import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;

import javax.batch.runtime.BatchRuntime;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ManualConfiguration {
    public static void main(final String... args) throws Throwable {
        final ClassLoader tccl = Tccl.get();
        final DeploymentModelImpl model = new SubSystemModelImpl(tccl).getDeployment(Constants.DEFAULT);
        final JobOperatorModelImpl op = setDefaults(model, tccl);
        try (final TheEnvironment environment = configureEnvironment(model, op, tccl)) {
            Chainlink.setEnvironment(environment);
            BatchRuntime.getJobOperator().start("a_job", new Properties());
        } finally {
            Chainlink.setEnvironment(null);
        }
    }

    public static JobOperatorModelImpl setDefaults(final DeploymentModelImpl model, final ClassLoader tccl) {
        // Set defaults for the default JobOperator
        final JobOperatorModelImpl op = model.getJobOperator(Constants.DEFAULT);
        op.getClassLoader().setDefaultFactory(new ClassLoaderFactoryImpl(tccl));
        op.getTransactionManager().setDefaultFactory(new LocalTransactionManagerFactory());
        op.getRepository().setDefaultFactory(new MemoryRepositoryFactory());
        op.getMarshalling().setDefaultFactory(new JdkMarshallingFactory());
        op.getTransport().setDefaultFactory(new LocalTransportFactory());
        op.getRegistry().setDefaultFactory(new LocalRegistryFactory());
        op.getExecutor().setDefaultFactory(new EventedExecutorFactory());
        return op;
    }

    public static TheEnvironment configureEnvironment(final DeploymentModelImpl model, final JobOperatorModelImpl op, final ClassLoader tccl) throws Exception {
        // Configure from "chainlink.xml" in root of classpath
        model.loadChainlinkXml();

        final JobOperatorImpl operator = op.createJobOperator();

        return new TheEnvironment(operator);
    }

    public static class TheEnvironment implements Environment, AutoCloseable {
        final JobOperatorImpl operator;

        TheEnvironment(final JobOperatorImpl operator) {
            this.operator = operator;
        }

        @Override
        public JobOperatorImpl getJobOperator(final String name) throws NoConfigurationWithIdException {
            if (Constants.DEFAULT.equals(name)) {
                return operator;
            }
            throw new NoConfigurationWithIdException("No operator for name " + name);
        }

        @Override
        public SubSystemSchema<?,?,?> getConfiguration() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public SubSystemSchema<?,?,?> setConfiguration(final Configure configure) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public void reload() throws Exception {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public void close() throws Exception {
            operator.close();
        }
    }
}
