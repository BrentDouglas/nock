package io.machinecode.chainlink.core.configuration.op;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public interface Creator<X> {

    X create() throws Exception;
}
