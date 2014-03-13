package io.machinecode.chainlink.jsl.fluent.task;

import io.machinecode.chainlink.spi.element.task.Batchlet;
import io.machinecode.chainlink.jsl.fluent.FluentPropertyReference;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentBatchlet extends FluentPropertyReference<FluentBatchlet> implements FluentTask<FluentBatchlet>, Batchlet {

    @Override
    public FluentBatchlet copy() {
        return copy(new FluentBatchlet());
    }
}