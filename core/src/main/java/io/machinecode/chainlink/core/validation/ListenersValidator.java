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
package io.machinecode.chainlink.core.validation;

import io.machinecode.chainlink.core.validation.visitor.ValidatingVisitor;
import io.machinecode.chainlink.core.validation.visitor.VisitorNode;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.jsl.Listener;
import io.machinecode.chainlink.spi.jsl.Listeners;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ListenersValidator extends ValidatingVisitor<Listeners> {

    public static final ListenersValidator INSTANCE = new ListenersValidator();

    protected ListenersValidator() {
        super(Listeners.ELEMENT);
    }

    @Override
    public void doVisit(final Listeners that, final VisitorNode context) {
        for(final Listener listener : that.getListeners()) {
            if (listener == null) {
                context.addProblem(Messages.format("CHAINLINK-002100.validation.not.null.element", "listener"));
                continue;
            }
            ListenerValidator.INSTANCE.visit(listener, context);
        }
    }
}
