package io.machinecode.nock.core.model.task;

import io.machinecode.nock.core.loader.ArtifactReferenceImpl;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.element.task.ItemReader;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.inject.InjectablesProvider;
import io.machinecode.nock.spi.inject.InjectionContext;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemReaderImpl extends PropertyReferenceImpl<javax.batch.api.chunk.ItemReader> implements ItemReader {

    public ItemReaderImpl(final ArtifactReferenceImpl ref, final PropertiesImpl properties) {
        super(ref, properties);
    }

    public void open(final Executor executor, final ExecutionContext context, final Serializable checkpoint) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(javax.batch.api.chunk.ItemReader.class, injectionContext, context).open(checkpoint);
        } finally {
            provider.setInjectables(null);
        }
    }

    public void close(final Executor executor, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(javax.batch.api.chunk.ItemReader.class, injectionContext, context).close();
        } finally {
            provider.setInjectables(null);
        }
    }

    public Object readItem(final Executor executor, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            return load(javax.batch.api.chunk.ItemReader.class, injectionContext, context).readItem();
        } finally {
            provider.setInjectables(null);
        }
    }

    public Serializable checkpointInfo(final Executor executor, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            return load(javax.batch.api.chunk.ItemReader.class, injectionContext, context).checkpointInfo();
        } finally {
            provider.setInjectables(null);
        }
    }
}
