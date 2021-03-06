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
package io.machinecode.chainlink.core.expression;

import io.machinecode.chainlink.core.property.SystemPropertyLookup;
import io.machinecode.chainlink.spi.jsl.Property;

import java.util.Properties;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class JobPropertyContext extends PropertyContext {

    final PropertyResolver parameters;
    final SystemResolver system;

    public JobPropertyContext(final Properties parameters, final SystemPropertyLookup system) {
        super(new PropertyResolver(Expression.JOB_PROPERTIES, Expression.JOB_PROPERTIES_LENGTH, new Properties()));
        this.parameters = new PropertyResolver(Expression.JOB_PARAMETERS, Expression.JOB_PARAMETERS_LENGTH, parameters);
        this.system = new SystemResolver(system);
    }

    public void addProperty(final Property property) {
        this.properties.properties.put(property.getName(), property.getValue());
    }
}
