package io.machinecode.chainlink.transport.infinispan.cmd;

import io.machinecode.chainlink.transport.infinispan.InfinispanRegistry;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.transport.infinispan.OnCommand;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.core.then.ChainImpl;
import org.infinispan.context.InvocationContext;
import org.infinispan.remoting.transport.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExecuteCommand extends BaseChainlinkCommand {

    public static final byte COMMAND_ID_61 = 61;

    WorkerId workerId;
    ExecutableEvent event;

    public ExecuteCommand(final String cacheName) {
        super(cacheName);
    }

    public ExecuteCommand(final String cacheName, final WorkerId workerId, final ExecutableEvent event) {
        super(cacheName, event.getExecutable().getContext().getJobExecutionId());
        this.workerId = workerId;
        this.event = event;
    }

    @Override
    public Object perform(final InvocationContext invocationContext) throws Throwable {
        registry.getWorker(workerId).execute(event);
        return null;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_ID_61;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{ jobExecutionId, workerId, event };
    }

    @Override
    public void init(final InfinispanRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void setParameters(final int commandId, final Object[] parameters) {
        super.setParameters(commandId, parameters);
        this.workerId = (WorkerId)parameters[1];
        this.event = (ExecutableEvent)parameters[2];
    }

    @Override
    public boolean isReturnValueExpected() {
        return false;
    }
}