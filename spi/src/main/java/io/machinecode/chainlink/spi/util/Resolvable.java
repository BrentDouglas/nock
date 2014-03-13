package io.machinecode.chainlink.spi.util;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Resolvable<T> {

    T resolve(final ClassLoader loader) throws Exception;
}