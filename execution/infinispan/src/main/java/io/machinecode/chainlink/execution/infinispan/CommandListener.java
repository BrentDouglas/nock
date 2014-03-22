package io.machinecode.chainlink.execution.infinispan;

import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.deferred.Listener;
import org.infinispan.commands.ReplicableCommand;
import org.infinispan.remoting.transport.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CommandListener implements Listener {

    final Address address;
    final ReplicableCommand command;
    final InfinispanExecutor executor;

    public CommandListener(final Address address, final ReplicableCommand command, final InfinispanExecutor executor) {
        this.address = address;
        this.command = command;
        this.executor = executor;
    }

    @Override
    public void run(final Deferred<?> that) {
        executor.invoke(address, command);
    }
}
