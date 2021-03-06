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
package io.machinecode.chainlink.core.configuration;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.core.inject.ClosableScopeImpl;
import io.machinecode.chainlink.core.schema.xml.subsystem.XmlChainlinkSubSystem;
import io.machinecode.chainlink.spi.configuration.SubSystemModel;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class SubSystemModelImpl extends ScopeModelImpl implements SubSystemModel {

    final Map<String, DeploymentModelImpl> deployments;
    final DeploymentModelImpl defaultDeployment;

    public SubSystemModelImpl(final ClassLoader loader) {
        super(new WeakReference<>(loader), new THashSet<String>(), new ClosableScopeImpl());
        this.deployments = new THashMap<>();
        this.deployments.put(Constants.DEFAULT, defaultDeployment = new DeploymentModelImpl(this, new ClosableScopeImpl()));
    }

    @Override
    public DeploymentModelImpl getDeployment(final String name) {
        DeploymentModelImpl deployment = deployments.get(name);
        if (deployment == null) {
            deployments.put(name, deployment = new DeploymentModelImpl(this, new ClosableScopeImpl()));
        }
        return deployment;
    }

    public DeploymentModelImpl findDeployment(final String name) {
        DeploymentModelImpl scope = deployments.get(name);
        if (scope == null) {
            return defaultDeployment;
        }
        return scope;
    }

    public SubSystemModel loadChainlinkSubsystemXml(final InputStream stream) throws Exception {
        Model.configureSubSystem(this, XmlChainlinkSubSystem.read(stream), loader.get());
        return this;
    }

    @Override
    public void close() throws Exception {
        Exception exception = null;
        for (final DeploymentModelImpl deployment : deployments.values()) {
            try {
                deployment.close();
            } catch (final Exception e) {
                if (exception == null) {
                    exception = e;
                } else {
                    exception.addSuppressed(e);
                }
            }
        }
        try {
            super.close();
        } catch (final Exception e) {
            if (exception == null) {
                exception = e;
            } else {
                exception.addSuppressed(e);
            }
        }
        if (exception != null) {
            throw exception;
        }
    }
}
