package io.machinecode.chainlink.core.schema.xml;

import io.machinecode.chainlink.core.schema.JobOperatorSchema;
import io.machinecode.chainlink.core.schema.JobOperatorWithNameExistsException;
import io.machinecode.chainlink.core.schema.MutableScopeSchema;
import io.machinecode.chainlink.core.schema.NoJobOperatorWithNameException;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
public abstract class XmlScope implements MutableScopeSchema<XmlDeclaration, XmlProperty, XmlJobOperator> {

    @XmlAttribute(name = "ref", required = false)
    protected String ref;

    @XmlElement(name = "property", namespace = XmlChainlink.NAMESPACE, required = false)
    private List<XmlProperty> properties = new ArrayList<>(0);

    @Override
    public String getRef() {
        return ref;
    }

    @Override
    public void setRef(final String ref) {
        this.ref = ref;
    }

    @Override
    public void setProperties(final List<XmlProperty> properties) {
        this.properties = properties;
    }

    @Override
    public List<XmlProperty> getProperties() {
        return this.properties;
    }

    @Override
    public XmlJobOperator getJobOperator(final String name) {
        for (final XmlJobOperator operator : this.getJobOperators()) {
            if (name.equals(operator.getName())) {
                return operator;
            }
        }
        return null;
    }

    @Override
    public XmlJobOperator removeJobOperator(final String name) throws NoJobOperatorWithNameException {
        final ListIterator<XmlJobOperator> it = this.getJobOperators().listIterator();
        while (it.hasNext()) {
            final XmlJobOperator that = it.next();
            if (name.equals(that.getName())) {
                it.remove();
                return that;
            }
        }
        throw new NoJobOperatorWithNameException("No job operator with name " + name);
    }

    @Override
    public void addJobOperator(final JobOperatorSchema<?,?> jobOperator) throws Exception {
        if (getJobOperator(jobOperator.getName()) != null) {
            throw new JobOperatorWithNameExistsException("A job operator already exists with name " + jobOperator.getName());
        }
        final XmlJobOperator op = new XmlJobOperator();
        op.accept(jobOperator);
        getJobOperators().add(op);
    }
}
