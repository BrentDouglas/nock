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
package io.machinecode.chainlink.core.jsl.xml;

import io.machinecode.chainlink.spi.jsl.inherit.InheritableBase;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * An elementName that can inherit from other elements from
 * the JSL Inheritance v1 spec.
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
@XmlAccessorType(NONE)
public abstract class XmlInheritable<T extends XmlInheritable<T>> implements InheritableBase<T> {

    @XmlAttribute(name = "abstract", required = false)
    protected Boolean _abstract;

    @XmlAttribute(name = "parent", required = false)
    protected String parent;

    @XmlAttribute(name = "jsl-name", required = false)
    protected String jslName;


    @Override
    public Boolean isAbstract() {
        return _abstract;
    }

    @Override
    public T setAbstract(final Boolean _abstract) {
        this._abstract = _abstract;
        return (T)this;
    }

    @Override
    public String getParent() {
        return parent;
    }

    @Override
    public T setParent(final String parent) {
        this.parent = parent;
        return (T)this;
    }

    @Override
    public String getJslName() {
        return jslName;
    }

    @Override
    public T setJslName(final String jslName) {
        this.jslName = jslName;
        return (T)this;
    }

    @Override
    public T copy(final T that) {
        return BaseTool.copy((T)this, that);
        //that.setAbstract(this._abstract);
        //that.setParent(this.parent);
        //that.setJslName(this.jslName);
        //return that;
    }

    protected void inheritingElementRule(final T parent) {
        BaseTool.inheritingElementRule(this, parent);
    }
}
