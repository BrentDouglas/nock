package io.machinecode.chainlink.test.core.execution.artifact.chunk.writer;

import io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent;
import io.machinecode.chainlink.test.core.execution.artifact.chunk.EventOrderAccumulator;
import io.machinecode.chainlink.test.core.execution.artifact.chunk.exception.FailWriteCloseException;
import io.machinecode.chainlink.test.core.execution.artifact.chunk.exception.FailWriteException;

import javax.batch.api.chunk.ItemWriter;
import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class FailWriteAndCloseEventOrderWriter implements ItemWriter {

    private int count = 0;

    @Override
    public void open(final Serializable checkpoint) throws Exception {
        EventOrderAccumulator._order.add(ChunkEvent.WRITER_OPEN);
    }

    @Override
    public void close() throws Exception {
        EventOrderAccumulator._order.add(ChunkEvent.WRITER_CLOSE);
        throw new FailWriteCloseException();
    }

    @Override
    public void writeItems(final List<Object> items) throws Exception {
        EventOrderAccumulator._order.add(ChunkEvent.WRITE);
        if (count == 1) {
            throw new FailWriteException();
        } else {
            ++count;
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        EventOrderAccumulator._order.add(ChunkEvent.WRITER_CHECKPOINT);
        return null;
    }
}
