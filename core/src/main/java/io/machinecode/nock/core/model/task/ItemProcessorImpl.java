package io.machinecode.nock.core.model.task;

import io.machinecode.nock.core.loader.ArtifactReferenceImpl;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.element.task.ItemProcessor;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.inject.InjectablesProvider;
import io.machinecode.nock.spi.inject.InjectionContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemProcessorImpl extends PropertyReferenceImpl<javax.batch.api.chunk.ItemProcessor> implements ItemProcessor {

    public ItemProcessorImpl(final ArtifactReferenceImpl ref, final PropertiesImpl properties) {
        super(ref, properties);
    }

    public Object processItem(final Executor executor, final ExecutionContext context, final Object item) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            return load(javax.batch.api.chunk.ItemProcessor.class, injectionContext, context).processItem(item);
        } finally {
            provider.setInjectables(null);
        }
    }
}
