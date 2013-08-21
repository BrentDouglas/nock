package io.machinecode.nock.core.descriptor.transition;

import io.machinecode.nock.spi.element.transition.Fail;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FailImpl extends TransitionImpl implements Fail {

    private final String exitStatus;

    public FailImpl(final String on, final String exitStatus) {
        super(on);
        this.exitStatus = exitStatus;
    }

    @Override
    public String getExitStatus() {
        return this.exitStatus;
    }
}