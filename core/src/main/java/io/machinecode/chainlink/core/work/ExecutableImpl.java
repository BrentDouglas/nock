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
package io.machinecode.chainlink.core.work;

import io.machinecode.chainlink.core.context.ExecutionContextImpl;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.RepositoryId;
import io.machinecode.chainlink.spi.then.Chain;
import org.jboss.logging.Logger;

import java.io.Serializable;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public abstract class ExecutableImpl<T> implements Executable, Serializable {

    protected final T work;
    protected final WorkerId workerId;
    protected final RepositoryId repositoryId;
    protected final ExecutableId parentId;
    protected final ExecutionContextImpl context;

    public ExecutableImpl(final ExecutableId parentId, final ExecutionContextImpl context,
                          final T work, final RepositoryId repositoryId, final WorkerId workerId) {
        this.parentId = parentId;
        this.context = context;
        this.work = work;
        this.workerId = workerId;
        this.repositoryId = repositoryId;
    }

    public ExecutableImpl(final ExecutableId parentId, final ExecutableImpl<T> executable, final WorkerId workerId) {
        this(parentId, executable.context, executable.work, executable.repositoryId, workerId);
    }

    @Override
    public ExecutableId getId() {
        return null;
    }

    @Override
    public ExecutionContext getContext() {
        return context;
    }

    @Override
    public WorkerId getWorkerId() {
        return workerId;
    }

    @Override
    public void execute(final Configuration configuration, final Chain<?> chain, final WorkerId workerId,
                        final ExecutionContext previous) {
        try {
            log().tracef(Messages.get("CHAINLINK-015700.executable.execute"), this.context, this);
            doExecute(configuration, chain, workerId, this.parentId, previous);
        } catch (final Throwable e) {
            log().errorf(e, Messages.get("CHAINLINK-015701.executable.exception"), this.context, this);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{workerId=" + workerId + ",work=" + work + "}";
    }

    protected abstract void doExecute(final Configuration configuration, final Chain<?> chain, final WorkerId workerId,
                                      final ExecutableId parentId, final ExecutionContext previous) throws Throwable;

    protected abstract Logger log();
}
