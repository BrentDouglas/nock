package io.machinecode.chainlink.spi.marshalling;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Marshalling {

    <T> T clone(final T that) throws ClassNotFoundException, IOException;

    byte[] marshall(final Serializable that) throws IOException;

    byte[] marshall(final Serializable... that) throws IOException;

    Serializable unmarshall(final byte[] that, final ClassLoader loader) throws ClassNotFoundException, IOException;

    <T extends Serializable> T unmarshall(final byte[] that, final Class<T> clazz, final ClassLoader loader) throws ClassNotFoundException, IOException;
}