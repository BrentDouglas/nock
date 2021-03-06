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
package io.machinecode.chainlink.rt.wildfly.schema;

import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.OperationStepHandler;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.PersistentResourceDefinition;
import org.jboss.as.controller.descriptions.ResourceDescriptionResolver;

import java.util.LinkedList;
import java.util.List;

import static org.jboss.as.controller.registry.OperationEntry.Flag;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ElementBuilder {

    private final PathElement path;
    private final ResourceDescriptionResolver descriptionResolver;
    private org.jboss.as.controller.OperationStepHandler addHandler;
    private OperationStepHandler removeHandler;
    private Flag addRestartLevel = Flag.RESTART_NONE;
    private Flag removeRestartLevel = Flag.RESTART_RESOURCE_SERVICES;
    private List<AttributeDefinition> attributes = new LinkedList<>();
    private List<PersistentResourceDefinition> children = new LinkedList<>();

    public ElementBuilder(final PathElement path, final ResourceDescriptionResolver descriptionResolver) {
        this.path = path;
        this.descriptionResolver = descriptionResolver;
    }

    public ElementBuilder setAddHandler(final OperationStepHandler addHandler) {
        this.addHandler = addHandler;
        return this;
    }

    public ElementBuilder setRemoveHandler(final OperationStepHandler removeHandler) {
        this.removeHandler = removeHandler;
        return this;
    }

    public ElementBuilder setAddRestartLevel(final Flag addRestartLevel) {
        this.addRestartLevel = addRestartLevel;
        return this;
    }

    public ElementBuilder setRemoveRestartLevel(final Flag removeRestartLevel) {
        this.removeRestartLevel = removeRestartLevel;
        return this;
    }

    public ElementBuilder addAttribute(final AttributeDefinition attribute) {
        this.attributes.add(attribute);
        return this;
    }

    public ElementBuilder addChild(final PersistentResourceDefinition child) {
        this.children.add(child);
        return this;
    }

    public Element build() {
        return new Element(
                path, descriptionResolver, addHandler, removeHandler, addRestartLevel, removeRestartLevel,
                attributes.toArray(new AttributeDefinition[attributes.size()]),
                children.toArray(new PersistentResourceDefinition[children.size()])
        );
    }
}
