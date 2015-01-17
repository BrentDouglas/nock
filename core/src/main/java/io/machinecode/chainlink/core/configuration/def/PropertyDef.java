package io.machinecode.chainlink.core.configuration.def;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface PropertyDef {

    String getName();

    void setName(final String name);

    String getValue();

    void setValue(final String value);
}