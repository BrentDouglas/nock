package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.configuration.factory.SerializerFactory;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.spi.loader.JobLoader;
import io.machinecode.chainlink.spi.security.SecurityCheck;
import io.machinecode.chainlink.spi.then.When;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface RepositoryConfiguration extends SecurityConfiguration {

    JobLoader getJobLoader();

    ArtifactLoader getArtifactLoader();

    Injector getInjector();

    SecurityCheck getSecurityCheck();

    SerializerFactory getSerializerFactory();

    When getWhen();
}