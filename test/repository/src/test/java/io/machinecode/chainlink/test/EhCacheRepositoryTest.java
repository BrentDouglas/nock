package io.machinecode.chainlink.test;

import io.machinecode.chainlink.repository.ehcache.EhCacheExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.RepositoryTest;
import net.sf.ehcache.CacheManager;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class EhCacheRepositoryTest extends RepositoryTest {

    private static final CacheManager manager = new CacheManager();

    @Override
    protected ExecutionRepository _repository() throws Exception {
        return new EhCacheExecutionRepository(
                marshallerFactory().produce(null),
                manager
        );
    }
}