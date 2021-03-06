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
package io.machinecode.chainlink.core.inject.batch;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static io.machinecode.chainlink.core.inject.batch.BatchArtifacts.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlRootElement(namespace = NAMESPACE, name = BatchArtifacts.ELEMENT)
@XmlAccessorType(NONE)
public class BatchArtifacts {

    public static final String ELEMENT = "batch-artifacts";

    public static final String SCHEMA_URL = "http://xmlns.jcp.org/xml/ns/javaee/batchXML_1_0.xsd";
    public static final String NAMESPACE = "http://xmlns.jcp.org/xml/ns/javaee";


    @XmlElement(name = BatchArtifactRef.ELEMENT, namespace = NAMESPACE, required = false)
    private List<BatchArtifactRef> refs = new ArrayList<>(0);

    public List<BatchArtifactRef> getRefs() {
        return refs;
    }

    public void setRefs(final List<BatchArtifactRef> refs) {
        this.refs = refs;
    }
}
