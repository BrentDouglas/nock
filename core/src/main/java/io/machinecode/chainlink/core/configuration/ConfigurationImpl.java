package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.loader.JobLoader;

import javax.transaction.TransactionManager;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ConfigurationImpl implements Configuration {

    private static final JobLoader[] JOB_LOADERS = new JobLoader[0];
    private static final ArtifactLoader[] ARTIFACT_LOADERS = new ArtifactLoader[0];
    private static final Injector[] INJECTORS = new Injector[0];

    private final ClassLoader loader;
    private final ExecutionRepository repository;
    private final TransactionManager transactionManager;
    private final JobLoader[] jobLoaders;
    private final ArtifactLoader[] artifactLoaders;
    private final Injector[] injectors;

    public ConfigurationImpl(final Configuration configuration) {
        this.loader = configuration.getClassLoader();
        this.repository = configuration.getRepository();
        this.transactionManager = configuration.getTransactionManager() == null
                ? new LocalTransactionManager(180, TimeUnit.SECONDS)
                : configuration.getTransactionManager();
        this.jobLoaders = configuration.getJobLoaders();
        this.artifactLoaders = configuration.getArtifactLoaders();
        this.injectors = configuration.getInjectors();
    }

    public ConfigurationImpl(final Builder builder) {
        this.loader = builder.loader;
        this.repository = builder.repository;
        this.transactionManager = builder.transactionManager;
        this.jobLoaders = builder.jobLoaders;
        this.artifactLoaders = builder.artifactLoaders;
        this.injectors = builder.injectors;
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.loader;
    }

    @Override
    public ExecutionRepository getRepository() {
        return this.repository;
    }

    @Override
    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    @Override
    public JobLoader[] getJobLoaders() {
        return this.jobLoaders;
    }

    @Override
    public ArtifactLoader[] getArtifactLoaders() {
        return this.artifactLoaders;
    }

    @Override
    public Injector[] getInjectors() {
        return this.injectors;
    }

    public static class Builder {
        private ClassLoader loader = Thread.currentThread().getContextClassLoader();
        private ExecutionRepository repository;
        private TransactionManager transactionManager;
        private JobLoader[] jobLoaders = JOB_LOADERS;
        private ArtifactLoader[] artifactLoaders = ARTIFACT_LOADERS;
        private Injector[] injectors = INJECTORS;

        public Builder setLoader(final ClassLoader loader) {
            this.loader = loader;
            return this;
        }

        public Builder setRepository(final ExecutionRepository repository) {
            this.repository = repository;
            return this;
        }

        public Builder setTransactionManager(final TransactionManager transactionManager) {
            this.transactionManager = transactionManager;
            return this;
        }

        public Builder setJobLoaders(final JobLoader... jobLoaders) {
            this.jobLoaders = jobLoaders;
            return this;
        }

        public Builder setArtifactLoaders(final ArtifactLoader... artifactLoaders) {
            this.artifactLoaders = artifactLoaders;
            return this;
        }

        public Builder setInjectors(final Injector... injectors) {
            this.injectors = injectors;
            return this;
        }

        public ConfigurationImpl build() {
            return new ConfigurationImpl(this);
        }
    }
}