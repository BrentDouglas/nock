/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package io.machinecode.chainlink.core.transport.cmd;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.execution.CallbackEvent;
import io.machinecode.chainlink.spi.execution.WorkerId;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CallbackCommand implements Command<Object> {
    private static final long serialVersionUID = 1L;

    final WorkerId workerId;
    final CallbackEvent event;

    public CallbackCommand(final WorkerId workerId, final CallbackEvent event) {
        this.workerId = workerId;
        this.event = event;
    }

    @Override
    public Object perform(final Configuration configuration, final Object origin) throws Throwable {
        configuration.getExecutor().getWorker(workerId).callback(event);
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CallbackCommand{");
        sb.append("workerId=").append(workerId);
        sb.append(", event=").append(event);
        sb.append('}');
        return sb.toString();
    }
}
