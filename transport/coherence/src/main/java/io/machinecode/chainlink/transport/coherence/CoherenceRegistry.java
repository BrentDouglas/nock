package io.machinecode.chainlink.transport.coherence;

import com.tangosol.net.AbstractInvocable;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.Cluster;
import com.tangosol.net.InvocationObserver;
import com.tangosol.net.InvocationService;
import com.tangosol.net.Member;
import com.tangosol.util.UID;
import io.machinecode.chainlink.spi.configuration.RegistryConfiguration;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.transport.core.BaseDistributedRegistry;
import io.machinecode.chainlink.transport.core.DistributedExecutionRepositoryProxy;
import io.machinecode.chainlink.transport.core.DistributedRegistry;
import io.machinecode.chainlink.transport.core.DistributedWorker;
import io.machinecode.chainlink.transport.core.cmd.DistributedCommand;
import io.machinecode.then.api.Promise;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CoherenceRegistry extends BaseDistributedRegistry<Member, CoherenceRegistry> implements DistributedRegistry<Member, CoherenceRegistry> {

    private static final Logger log = Logger.getLogger(CoherenceRegistry.class);

    final String invocationServiceName;
    final InvocationService executor;

    final Member local;
    protected volatile List<Member> remotes;

    public CoherenceRegistry(final RegistryConfiguration configuration, final String invocationServiceName) throws Exception {
        super(configuration);
        this.invocationServiceName = invocationServiceName;
        this.executor = (InvocationService) CacheFactory.getService(invocationServiceName);

        executor.setUserContext(this);

        final Cluster cluster = CacheFactory.getCluster();
        this.local = cluster.getLocalMember();
        this.remotes = _remoteMembers(cluster.getMemberSet());
    }

    @Override
    public void startup() {
        super.startup();
        log.infof("CoherenceRegistry started on address: [%s]", this.local); //TODO Message
    }

    @Override
    public void shutdown() {
        log.infof("CoherenceRegistry is shutting down."); //TODO Message
        this.executor.shutdown();
        super.shutdown();
    }

    @Override
    public ExecutableId generateExecutableId() {
        return new CoherenceUUIDId(local);
    }

    @Override
    public WorkerId generateWorkerId(final Worker worker) {
        if (worker instanceof Thread) {
            return new CoherenceWorkerId((Thread)worker, local);
        } else {
            return new CoherenceUUIDId(local);
        }
    }

    @Override
    public ExecutionRepositoryId generateExecutionRepositoryId() {
        return new CoherenceUUIDId(local);
    }

    @Override
    public Member getLocal() {
        return local;
    }

    @Override
    protected List<Member> getRemotes() {
        return this.remotes;
    }

    @Override
    protected DistributedWorker<Member, CoherenceRegistry> createDistributedWorker(final Member address, final WorkerId workerId) {
        return new CoherenceWorker(this, this.local, address, workerId);
    }

    @Override
    protected DistributedExecutionRepositoryProxy<Member, CoherenceRegistry> createDistributedExecutionRepository(final ExecutionRepositoryId id, final Member address) {
        return new CoherenceExecutionRepositoryProxy(this, id, address);
    }

    @Override
    public <T> void invoke(final Member address, final DistributedCommand<T, Member, CoherenceRegistry> command, final Promise<T, Throwable> promise) {
        try {
            log.tracef("Invoking %s on %s.", command, address);
            this.executor.execute(
                    new Invocation(command, this.local, invocationServiceName),
                    Collections.singleton(address),
                    new InvocationObserver() {
                        @Override
                        public void memberCompleted(final Member member, final Object o) {
                            promise.resolve((T)o);
                        }

                        @Override
                        public void memberFailed(final Member member, final Throwable throwable) {
                            promise.reject(throwable);
                        }

                        @Override
                        public void memberLeft(final Member member) {
                            promise.reject(new Exception()); //TODO Message
                        }

                        @Override
                        public void invocationCompleted() {}
                    }
            );
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @Override
    public <T> void invoke(final Member address, final DistributedCommand<T, Member, CoherenceRegistry> command, final Promise<T, Throwable> promise, final long timeout, final TimeUnit unit) {
        invoke(address, command, promise);
    }

    public static class Invocation extends AbstractInvocable implements Serializable {
        private final DistributedCommand<?, Member, CoherenceRegistry> command;
        private final UID uuid;
        private final String invocationServiceName;
        private transient CoherenceRegistry registry;
        private transient Member origin;

        public Invocation(final DistributedCommand<?, Member, CoherenceRegistry> command, final Member origin, final String invocationServiceName) {
            this.command = command;
            this.invocationServiceName = invocationServiceName;
            this.uuid = origin.getUid();
        }

        @Override
        public void run() {
            this.registry = (CoherenceRegistry) CacheFactory.getService(this.invocationServiceName).getUserContext();
            for (final Object that : CacheFactory.getCluster().getMemberSet()) {
                final Member member = (Member) that;
                if (member.getUid().equals(this.uuid)) {
                    this.origin = member;
                    break;
                }
            }
            if (this.origin == null) {
                throw new IllegalStateException(); //TODO Message
            }
            try {
                command.perform(this.registry, this.origin);
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}