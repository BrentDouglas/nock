package io.machinecode.nock.spi.element;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Properties extends Element {

    String ELEMENT = "properties";

    List<? extends Property> getProperties();

    String getPartition();
}