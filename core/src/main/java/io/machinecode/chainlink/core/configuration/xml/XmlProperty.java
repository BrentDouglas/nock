package io.machinecode.chainlink.core.configuration.xml;

import io.machinecode.chainlink.core.configuration.def.PropertyDef;
import io.machinecode.chainlink.spi.configuration.PropertyModel;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
public class XmlProperty implements PropertyDef {

    public static final String ELEMENT = "property";

    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlAttribute(name = "value", required = true)
    private String value;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(final String value) {
        this.value = value;
    }

    public static void convert(final List<XmlProperty> properties, final PropertyModel target) {
        for (final XmlProperty property : properties) {
            target.setProperty(property.getName(), property.getValue());
        }
    }
}
