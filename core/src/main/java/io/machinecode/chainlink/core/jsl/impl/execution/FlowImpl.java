/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.core.jsl.impl.execution;

import io.machinecode.chainlink.core.context.ExecutionContextImpl;
import io.machinecode.chainlink.core.context.JobContextImpl;
import io.machinecode.chainlink.core.jsl.impl.JobImpl;
import io.machinecode.chainlink.core.jsl.impl.transition.TransitionImpl;
import io.machinecode.chainlink.core.util.Statuses;
import io.machinecode.chainlink.core.work.ExecutionExecutable;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.jsl.execution.Flow;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.RepositoryId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.Promise;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FlowImpl extends ExecutionImpl implements Flow {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(FlowImpl.class);

    private final String next;
    private final List<ExecutionImpl> executions;
    private final List<TransitionImpl> transitions;

    public FlowImpl(final String id, final String next, final List<ExecutionImpl> executions,
                    final List<TransitionImpl> transitions) {
        super(id);
        this.next = next;
        this.executions = executions;
        this.transitions = transitions;
    }

    @Override
    public String getNext() {
        return this.next;
    }

    @Override
    public List<ExecutionImpl> getExecutions() {
        return this.executions;
    }

    @Override
    public List<TransitionImpl> getTransitions() {
        return this.transitions;
    }

    @Override
    public Promise<Chain<?>,Throwable,?> before(final JobImpl job, final Configuration configuration, final RepositoryId repositoryId,
                           final WorkerId workerId, final ExecutableId callbackId, final ExecutableId parentId,
                           final ExecutionContextImpl context) throws Exception {
        log.debugf(Messages.get("CHAINLINK-020000.flow.before"), context, id);
        final ExecutionImpl execution = this.executions.get(0);
        return _resolve(JobImpl.execute(configuration, new ExecutionExecutable(
                job,
                callbackId,
                execution,
                context,
                repositoryId,
                null
        )));
    }

    @Override
    public Promise<Chain<?>,Throwable,?> after(final JobImpl job, final Configuration configuration, final RepositoryId repositoryId,
                             final WorkerId workerId, final ExecutableId parentId, final ExecutionContextImpl context,
                             final ExecutionContext childContext) throws Exception {
        log.debugf(Messages.get("CHAINLINK-020001.flow.after"), context, id);
        final JobContextImpl jobContext = context.getJobContext();
        final BatchStatus batchStatus = jobContext.getBatchStatus();
        if (Statuses.isStopping(batchStatus) || Statuses.isFailed(batchStatus)) {
            return configuration.getTransport().callback(parentId, context);
        }
        final TransitionImpl transition = this.transition(context, this.transitions, jobContext.getBatchStatus(), jobContext.getExitStatus());
        if (transition != null && transition.isTerminating()) {
            return configuration.getTransport().callback(parentId, context);
        } else {
            return this.next(job, configuration, workerId, context, parentId, repositoryId, this.next, transition);
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
