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
package io.machinecode.chainlink.spi.jsl.inherit;

import io.machinecode.chainlink.spi.loader.InheritableJobLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Rules {

    public static <X extends Copyable<X>> X copy(final X that) {
        return that == null ? null : that.copy();
    }

    public static <X extends Copyable<X>> CopyList<X> copyList(final List<? extends X> that) {
        return that == null ? null : new CopyList<>(that);
    }

    public static <X extends Inheritable<X>> InheritList<X> inheritingList(final InheritableJobLoader repository, final String defaultJobXml, final List<? extends X> that) {
        return that == null ? null : new InheritList<>(repository, defaultJobXml, that);
    }

    public static <X extends Mergeable<X>> X merge(final X child, final X parent) {
        if (child == null) {
            return parent;
        }
        if (parent == null) {
            return child;
        }
        return child.merge(parent);
    }

    public static <X> X attributeRule(final X child, final X parent) {
        if (child == null) {
            return parent;
        }
        return child;
    }

    public static <X extends Copyable<X>> X elementRule(final X child, final X parent) {
        if (child == null) {
            return parent == null ? null : parent.copy();
        }
        return child.copy();
    }

    public static <X extends Mergeable<X>> X recursiveElementRule(final X child, final X parent, final InheritableJobLoader repository, final String defaultJobXml) {
        if (child == null) {
            return parent == null ? null : parent.copy();
        }
        final X that = child.copy();
        if (that instanceof InheritableBase) {
            ((Inheritable)that).inherit(repository, defaultJobXml);
        }
        return that.merge(parent);
    }

    public static <X extends Copyable<X>> List<X> listRule(final List<X> child, final List<X> parent) {
        if (child == null) {
            return new CopyList<>(parent);
        }
        if (parent == null) {
            return new CopyList<>(child);
        }
        return new CopyList<>(parent, child);
    }

    public static <X> List<X> listRule2(final List<X> child, final List<X> parent) {
        if (child == null) {
            return new ArrayList<>(parent);
        }
        if (parent == null) {
            return new ArrayList<>(child);
        }
        final List<X> that = new ArrayList<>(parent);
        that.addAll(child);
        return that;
    }
}
