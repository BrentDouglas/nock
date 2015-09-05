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
package javax.batch.runtime.context;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.Metric;
import java.io.Serializable;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface StepContext {

    String getStepName();

    Object getTransientUserData();

    void setTransientUserData(Object data);

    long getStepExecutionId();

    Properties getProperties();

    Serializable getPersistentUserData();

    void setPersistentUserData(Serializable data);

    BatchStatus getBatchStatus();

    String getExitStatus();

    void setExitStatus(String exitStatus);

    Exception getException();

    Metric[] getMetrics();
}
