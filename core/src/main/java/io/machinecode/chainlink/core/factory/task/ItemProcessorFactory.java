package io.machinecode.chainlink.core.factory.task;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.expression.PartitionPropertyContext;
import io.machinecode.chainlink.core.factory.PropertiesFactory;
import io.machinecode.chainlink.core.inject.ArtifactReferenceImpl;
import io.machinecode.chainlink.core.jsl.impl.PropertiesImpl;
import io.machinecode.chainlink.core.jsl.impl.task.ItemProcessorImpl;
import io.machinecode.chainlink.spi.jsl.task.ItemProcessor;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ItemProcessorFactory {

    public static ItemProcessorImpl produceExecution(final ItemProcessor that, final JobPropertyContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.produceExecution(that.getProperties(), context);
        return new ItemProcessorImpl(
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties
        );
    }

    public static ItemProcessorImpl producePartitioned(final ItemProcessorImpl that, final PartitionPropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.producePartitioned(that.getProperties(), context);
        return new ItemProcessorImpl(
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties
        );
    }
}
