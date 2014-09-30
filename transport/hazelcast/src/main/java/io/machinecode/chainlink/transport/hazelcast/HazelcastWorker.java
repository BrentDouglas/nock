package io.machinecode.chainlink.transport.hazelcast;

import com.hazelcast.core.Member;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.transport.core.cmd.DistributedCommand;
import io.machinecode.chainlink.transport.core.DistributedWorker;
import io.machinecode.chainlink.transport.hazelcast.cmd.HazelcastPushChainCommand;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class HazelcastWorker extends DistributedWorker<Member, HazelcastRegistry> {

    public HazelcastWorker(final HazelcastRegistry registry, final Member local, final Member remote, final WorkerId workerId) {
        super(registry, local, remote, workerId);
    }

    @Override
    protected DistributedCommand<ChainId, Member, HazelcastRegistry> createPushChainCommand(final long jobExecutionId, final ChainId localId) {
        return new HazelcastPushChainCommand(jobExecutionId, localId);
    }

    @Override
    protected Chain<?> createLocalChain(final long jobExecutionId, final ChainId remoteId) {
        return new HazelcastLocalChain(registry, remote, jobExecutionId, remoteId);
    }
}