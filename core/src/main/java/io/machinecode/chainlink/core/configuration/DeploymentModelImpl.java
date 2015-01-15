package io.machinecode.chainlink.core.configuration;

import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.spi.configuration.DeploymentModel;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class DeploymentModelImpl extends ScopeModelImpl implements DeploymentModel {

    public DeploymentModelImpl(final SubSystemModelImpl parent) {
        super(parent);
    }

    private DeploymentModelImpl(final DeploymentModelImpl that, final ClassLoader loader) {
        super(new WeakReference<>(loader), new THashSet<>(that.names), that.parent);
        for (final Map.Entry<String, JobOperatorModelImpl> entry : that.jobOperators.entrySet()) {
            this.jobOperators.put(entry.getKey(), entry.getValue().copy(this));
        }
    }

    public DeploymentModelImpl copy(final ClassLoader loader) {
        return new DeploymentModelImpl(this, loader);
    }
}
