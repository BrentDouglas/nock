package io.machinecode.chainlink.tck.core.repository;

import com.mongodb.MongoClient;
import io.machinecode.chainlink.repository.mongo.MongoRepository;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.RepositoryFactory;
import io.machinecode.chainlink.spi.repository.Repository;
import org.jongo.Jongo;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class MongoRepositoryFactory implements RepositoryFactory {
    @Override
    public Repository produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
        final String host = System.getProperty("mongo.host");
        final int port = Integer.parseInt(System.getProperty("mongo.port"));
        final String database = System.getProperty("mongo.database");
        final Jongo jongo = new Jongo(new MongoClient(host, port).getDB(database));
        jongo.getDatabase().dropDatabase();
        return new MongoRepository(jongo, dependencies.getMarshalling(), true);
    }
}
