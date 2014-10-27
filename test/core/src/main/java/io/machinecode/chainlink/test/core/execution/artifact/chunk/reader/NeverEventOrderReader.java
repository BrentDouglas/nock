package io.machinecode.chainlink.test.core.execution.artifact.chunk.reader;

import io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent;
import io.machinecode.chainlink.test.core.execution.artifact.chunk.EventOrderAccumulator;

import javax.batch.api.chunk.ItemReader;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class NeverEventOrderReader implements ItemReader {

    @Override
    public void open(final Serializable checkpoint) throws Exception {
        EventOrderAccumulator._order.add(ChunkEvent.READER_OPEN);
    }

    @Override
    public void close() throws Exception {
        EventOrderAccumulator._order.add(ChunkEvent.READER_CLOSE);
    }

    @Override
    public Object readItem() throws Exception {
        EventOrderAccumulator._order.add(ChunkEvent.READ);
        return null;
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        EventOrderAccumulator._order.add(ChunkEvent.READER_CHECKPOINT);
        return null;
    }
}
