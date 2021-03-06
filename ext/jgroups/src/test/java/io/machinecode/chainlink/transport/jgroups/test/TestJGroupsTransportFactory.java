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
package io.machinecode.chainlink.transport.jgroups.test;

import io.machinecode.chainlink.core.transport.TestTransportFactory;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.transport.jgroups.JGroupsTransport;
import org.jgroups.JChannel;
import org.jgroups.util.Util;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestJGroupsTransportFactory implements TestTransportFactory {

    final JChannel channel;

    public TestJGroupsTransportFactory() throws Exception {
        channel = new JChannel(Util.getTestStack());
        channel.connect("chainlink-jgroups-test");
    }

    @Override
    public Transport produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
        return new JGroupsTransport(
                dependencies,
                properties,
                channel
        );
    }

    @Override
    public void close() {
        channel.close();
    }
}
