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
package io.machinecode.chainlink.core.jsl.xml;

import io.machinecode.chainlink.core.jsl.xml.execution.XmlDecision;
import io.machinecode.chainlink.core.jsl.xml.execution.XmlExecution;
import io.machinecode.chainlink.core.jsl.xml.execution.XmlFlow;
import io.machinecode.chainlink.core.jsl.xml.execution.XmlSplit;
import io.machinecode.chainlink.core.jsl.xml.execution.XmlStep;
import io.machinecode.chainlink.spi.jsl.inherit.InheritableJob;
import io.machinecode.chainlink.spi.loader.InheritableJobLoader;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import java.util.ArrayList;
import java.util.List;

import static io.machinecode.chainlink.spi.jsl.Job.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlRootElement(namespace = NAMESPACE, name = "job")
@XmlAccessorType(NONE)
//@XmlType(name = "Job", propOrder = {
//        "properties",
//        "listeners",
//        "executions"
//})
public class XmlJob extends XmlInheritable<XmlJob> implements InheritableJob<XmlJob, XmlProperties, XmlListeners, XmlExecution> {

    @XmlID
    @XmlSchemaType(name = "ID")
    @XmlAttribute(name = "id", required = true)
    private String id;

    @XmlAttribute(name = "version", required = false)
    private static final String version = "1.0"; //fixed

    @XmlAttribute(name = "restartable", required = false)
    private String restartable = "true";

    @XmlElement(name = "properties", namespace = NAMESPACE, required = false, type = XmlProperties.class)
    private XmlProperties properties;

    @XmlElement(name = "listeners", namespace = NAMESPACE, required = false, type = XmlListeners.class)
    private XmlListeners listeners;

    @XmlElements({
            @XmlElement(name = "decision", required = false, namespace = NAMESPACE, type = XmlDecision.class),
            @XmlElement(name = "flow", required = false, namespace = NAMESPACE, type = XmlFlow.class),
            @XmlElement(name = "split", required = false, namespace = NAMESPACE, type = XmlSplit.class),
            @XmlElement(name = "step", required = false, namespace = NAMESPACE, type = XmlStep.class)
    })
    private List<XmlExecution> executions = new ArrayList<XmlExecution>(0);


    @Override
    public String getId() {
        return id;
    }

    public XmlJob setId(final String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getRestartable() {
        return restartable;
    }

    public XmlJob setRestartable(final String restartable) {
        this.restartable = restartable;
        return this;
    }

    @Override
    public XmlProperties getProperties() {
        return properties;
    }

    public XmlJob setProperties(final XmlProperties properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public XmlListeners getListeners() {
        return listeners;
    }

    public XmlJob setListeners(final XmlListeners listeners) {
        this.listeners = listeners;
        return this;
    }

    @Override
    public List<XmlExecution> getExecutions() {
        return executions;
    }

    public XmlJob setExecutions(final List<XmlExecution> executions) {
        this.executions = executions;
        return this;
    }

    @Override
    public XmlJob inherit(final InheritableJobLoader repository, final String defaultJobXml) {
        return JobTool.inherit(XmlJob.class, this, repository, defaultJobXml);
        //final XmlJob copy = this.copy();
        //if (copy.parent != null) {
        //    final XmlJob that = repository.findParent(XmlJob.class, copy, defaultJobXml);
//
        //    that.getExecutions().clear(); // 4.6.1.1
//
        //    copy.inheritingElementRule(that); // 4.6.1.2
//
        //    // 4.4
        //    copy.properties = Rules.merge(copy.properties, that.properties);
        //    copy.listeners = Rules.merge(copy.listeners, that.listeners);
        //    // 4.1
        //    copy.restartable = Rules.attributeRule(copy.restartable, that.restartable); // 4.4.1
        //}
        //copy.executions = Rules.inheritingList(repository, defaultJobXml, this.executions);
        //return copy;
    }

    @Override
    public XmlJob copy() {
        return copy(new XmlJob());
    }

    @Override
    public XmlJob copy(final XmlJob that) {
        return JobTool.copy(this, that);
    }
}
