package io.machinecode.nock.core.expression;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface PropertyResolver {

    CharSequence resolve(CharSequence value);
}