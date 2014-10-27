package io.machinecode.chainlink.test.core.execution.artifact.chunk.processor;

import io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent;
import io.machinecode.chainlink.test.core.execution.artifact.chunk.EventOrderAccumulator;
import io.machinecode.chainlink.test.core.execution.artifact.chunk.exception.FailProcessException;

import javax.batch.api.chunk.ItemProcessor;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class FailEventOrderProcessor implements ItemProcessor {

    int count = 0;

    @Override
    public Object processItem(final Object item) throws Exception {
        EventOrderAccumulator._order.add(ChunkEvent.PROCESS);
        if (count == 1) {
            throw new FailProcessException();
        } else {
            ++count;
            return item;
        }
    }
}
