package io.machinecode.chainlink.test.guice;

import io.machinecode.chainlink.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.chainlink.inject.core.VetoInjector;
import io.machinecode.chainlink.inject.guice.BindingProvider;
import io.machinecode.chainlink.inject.guice.GuiceArtifactLoader;
import io.machinecode.chainlink.repository.memory.MemoryExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.ExecutorTest;
import io.machinecode.chainlink.test.core.execution.artifact.batchlet.FailBatchlet;
import io.machinecode.chainlink.test.core.execution.artifact.batchlet.InjectedBatchlet;
import io.machinecode.chainlink.test.core.execution.artifact.batchlet.RunBatchlet;
import io.machinecode.chainlink.test.core.execution.artifact.batchlet.StopBatchlet;
import io.machinecode.chainlink.marshalling.jdk.JdkMarshaller;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.batch.api.Batchlet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
//@Ignore("Guice injector doesn't work properties deferring to the field name.")
public class GuiceExecutorTest extends ExecutorTest {

    @Override
    protected Builder _configuration() throws Exception {
        return super._configuration()
                .setArtifactLoaders(new GuiceArtifactLoader(new BindingProvider() {
                    @Override
                    public List<Binding> getBindings() {
                        return new ArrayList<Binding>() {{
                            add(Binding.of(Batchlet.class, "failBatchlet", FailBatchlet.class));
                            add(Binding.of(Batchlet.class, "runBatchlet", RunBatchlet.class));
                            add(Binding.of(Batchlet.class, "injectedBatchlet", InjectedBatchlet.class));
                            add(Binding.of(Batchlet.class, "stopBatchlet", StopBatchlet.class));
                        }};
                    }
                }))
                .setInjectors(new VetoInjector());
    }
    @Override
    protected ExecutionRepository _repository() {
        return new MemoryExecutionRepository(new JdkMarshaller());
    }

    @BeforeClass
    public static void beforeClass() {
        //
    }

    @AfterClass
    public static void afterClass() {
        //
    }
}
