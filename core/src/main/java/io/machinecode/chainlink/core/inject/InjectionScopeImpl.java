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
package io.machinecode.chainlink.core.inject;

import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.spi.inject.InjectionScope;

import java.util.Map;
import java.util.concurrent.Callable;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class InjectionScopeImpl extends ClosableScopeImpl implements InjectionScope {
    private final Map<String, Object> artifacts = new THashMap<>();

    @Override
    public <T> T getArtifact(final Class<T> clazz, final String ref, final Callable<T> call) throws Exception {
        T that = clazz.cast(artifacts.get(ref));
        if (that == null) {
            artifacts.put(ref, that = call.call());
        }
        return that;
    }
}
