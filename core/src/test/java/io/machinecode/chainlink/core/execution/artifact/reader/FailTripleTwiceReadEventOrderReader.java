package io.machinecode.chainlink.core.execution.artifact.reader;

import io.machinecode.chainlink.core.execution.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.core.execution.artifact.OrderEvent;
import io.machinecode.chainlink.core.execution.artifact.exception.FailReadException;

import javax.batch.api.chunk.ItemReader;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailTripleTwiceReadEventOrderReader implements ItemReader {

    private int count = 0;

    @Override
    public void open(final Serializable checkpoint) throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.READER_OPEN);
    }

    @Override
    public void close() throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.READER_CLOSE);
    }

    @Override
    public Object readItem() throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.READ);
        try {
            switch (count) {
                case 1:
                    throw new Exception();
                case 2:
                case 3:
                    throw new FailReadException();
                case 7:
                    throw new Exception();
                case 8:
                case 9:
                    throw new FailReadException();
                case 10:
                    return null;
                default:
                    return new Object();
            }
        } finally {
            ++count;
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.READER_CHECKPOINT);
        return null;
    }
}
