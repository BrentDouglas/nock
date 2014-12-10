package io.machinecode.chainlink.se.management;

import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.core.util.ResolvableService;
import io.machinecode.chainlink.core.configuration.xml.XmlChainlink;
import io.machinecode.chainlink.core.configuration.xml.XmlConfiguration;
import io.machinecode.chainlink.se.configuration.SeConfiguration;
import io.machinecode.chainlink.se.configuration.SeConfigurationDefaults;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.configuration.factory.ConfigurationFactory;
import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;
import io.machinecode.chainlink.spi.management.Environment;
import io.machinecode.chainlink.spi.util.Messages;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class SeEnvironment implements Environment {

    private final Map<String, JobOperatorImpl> operators = Collections.synchronizedMap(new THashMap<String, JobOperatorImpl>());
    private final String config;
    private boolean loaded = false;

    public SeEnvironment() {
        this(Constants.CHAINLINK_XML);
    }

    public SeEnvironment(final String config) {
        this.config = config == null
                ? Constants.CHAINLINK_XML
                : config;
    }

    @Override
    public JobOperatorImpl getJobOperator(final String name) throws NoConfigurationWithIdException {
        loadConfiguration();
        final JobOperatorImpl operator = operators.get(name);
        if (operator != null) {
            return operator;
        } else {
            throw new NoConfigurationWithIdException(Messages.format("CHAINLINK-031004.no.configuration.with.id", name));
        }
    }

    @Override
    public Map<String, JobOperatorImpl> getJobOperators() {
        loadConfiguration();
        return operators;
    }

    public void loadConfiguration() {
        if (loaded) {
            return;
        }
        synchronized (this) {
            if (!loaded) {
                _loadConfiguration();
            }
        }
    }

    private void _loadConfiguration() {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        final List<ConfigurationFactory> factories;
        try {
            factories = new ResolvableService<ConfigurationFactory>(Constants.CONFIGURATION_FACTORY_CLASS, ConfigurationFactory.class)
                    .resolve(tccl);
        } catch (final Exception e) {
            throw new RuntimeException(Messages.get("CHAINLINK-031001.configuration.exception"), e);
        }
        final SeConfigurationDefaults defaults = new SeConfigurationDefaults();
        if (factories.isEmpty()) {
            try {
                final JAXBContext context = JAXBContext.newInstance(XmlChainlink.class);
                final Unmarshaller unmarshaller = context.createUnmarshaller();
                final XmlChainlink xml = (XmlChainlink) unmarshaller.unmarshal(new File(config));

                for (final XmlConfiguration configuration : xml.getConfigurations()) {
                    operators.put(
                            configuration.getName(),
                            new JobOperatorImpl(SeConfiguration.xmlToBuilder(configuration)
                                    .setConfigurationDefaults(defaults)
                                    .build()
                            )
                    );
                }
            } catch (Exception e) {
                throw new IllegalStateException(Messages.get("CHAINLINK-031001.configuration.exception"), e);
            }
        } else {
            try {
                for (final ConfigurationFactory factory : factories) {
                    operators.put(
                            factory.getId(),
                            new JobOperatorImpl(factory
                                    .produce()
                                    .setConfigurationDefaults(defaults)
                                    .build()
                            )
                    );
                }
            } catch (final Exception e) {
                throw new IllegalStateException(Messages.get("CHAINLINK-031001.configuration.exception"), e);
            }
        }
        loaded = true;
    }
}
