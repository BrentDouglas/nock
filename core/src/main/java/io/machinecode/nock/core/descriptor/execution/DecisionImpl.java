package io.machinecode.nock.core.descriptor.execution;

import io.machinecode.nock.core.descriptor.PropertyReferenceImpl;
import io.machinecode.nock.core.descriptor.transition.TransitionImpl;
import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.execution.Decision;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DecisionImpl extends PropertyReferenceImpl implements Decision {

    private final String id;
    private final List<TransitionImpl> transitions;

    public DecisionImpl(final String id, final String ref, final Properties properties, final List<TransitionImpl> transitions) {
        super(ref, properties);
        this.id = id;
        this.transitions = transitions;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public List<TransitionImpl> getTransitions() {
        return this.transitions;
    }
}