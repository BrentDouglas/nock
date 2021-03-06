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
package io.machinecode.chainlink.core;

import io.machinecode.chainlink.core.util.Services;
import io.machinecode.chainlink.core.util.Tccl;
import io.machinecode.chainlink.spi.Messages;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public final class Chainlink {

    private static final Logger log = Logger.getLogger(Chainlink.class);

    private static volatile Environment environment;
    private static final Object lock = new Object();

    private Chainlink(){}

    public static void setEnvironment(final Environment environment) {
        synchronized (lock) {
            Chainlink.environment = environment;
            log.debugf("Setting environment to: %s", environment); // TODO Message
            lock.notifyAll();
        }
    }

    public static Environment getEnvironment() throws Exception {
        synchronized (lock) {
            if (Chainlink.environment != null) {
                return Chainlink.environment;
            }
            try {
                final ClassLoader tccl = Tccl.get();
                final List<Environment> environments = Services.load(Constants.ENVIRONMENT, Environment.class, tccl);
                if (!environments.isEmpty()) {
                    Chainlink.environment = environments.get(0);
                    return Chainlink.environment;
                }
            } catch (final ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(Messages.get("CHAINLINK-031001.configuration.exception"), e);
            }
            while (Chainlink.environment == null) {
                try {
                    log.debugf("Waiting for environment to be set."); // TODO Message
                    lock.wait();
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return Chainlink.environment;
        }
    }
}
