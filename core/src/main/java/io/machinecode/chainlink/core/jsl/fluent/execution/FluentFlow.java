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
package io.machinecode.chainlink.core.jsl.fluent.execution;

import io.machinecode.chainlink.core.jsl.fluent.FluentInheritable;
import io.machinecode.chainlink.core.jsl.fluent.transition.FluentTransition;
import io.machinecode.chainlink.spi.jsl.inherit.execution.InheritableFlow;
import io.machinecode.chainlink.spi.loader.InheritableJobLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentFlow extends FluentInheritable<FluentFlow> implements FluentExecution<FluentFlow>, InheritableFlow<FluentFlow, FluentExecution, FluentTransition> {

    private String id;
    private String next;
    private List<FluentExecution> executions = new ArrayList<>(0);
    private List<FluentTransition> transitions = new ArrayList<>(0);


    @Override
    public String getId() {
        return this.id;
    }

    public FluentFlow setId(final String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getNext() {
        return this.next;
    }

    public FluentFlow setNext(final String next) {
        this.next = next;
        return this;
    }

    @Override
    public List<FluentExecution> getExecutions() {
        return this.executions;
    }

    @Override
    public FluentFlow setExecutions(final List<FluentExecution> executions) {
        this.executions = executions;
        return this;
    }

    public FluentFlow addExecution(final FluentExecution execution) {
        this.executions.add(execution);
        return this;
    }

    @Override
    public List<FluentTransition> getTransitions() {
        return this.transitions;
    }

    @Override
    public FluentFlow setTransitions(final List<FluentTransition> transitions) {
        this.transitions = transitions;
        return this;
    }

    public FluentFlow addTransition(final FluentTransition transition) {
        this.transitions.add(transition);
        return this;
    }

    @Override
    public FluentFlow inherit(final InheritableJobLoader repository, final String defaultJobXml) {
        return FlowTool.inherit(FluentFlow.class, this, repository, defaultJobXml);
    }

    @Override
    public FluentFlow copy() {
        return copy(new FluentFlow());
    }

    @Override
    public FluentFlow copy(final FluentFlow that) {
        return FlowTool.copy(this, that);
    }
}
