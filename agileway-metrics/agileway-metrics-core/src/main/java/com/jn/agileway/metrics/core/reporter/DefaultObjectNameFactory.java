/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jn.agileway.metrics.core.reporter;

import com.jn.agileway.metrics.core.MetricName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class DefaultObjectNameFactory implements ObjectNameFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmxReporter.class);

    public ObjectName createName(String type, String domain, MetricName metricName) {
        String name = metricName.getKey();
        try {
            ObjectName objectName = new ObjectName(domain, "name", name);
            if (objectName.isPattern()) {
                objectName = new ObjectName(domain, "name", ObjectName.quote(name));
            }
            return objectName;
        } catch (MalformedObjectNameException e) {
            try {
                return new ObjectName(domain, "name", ObjectName.quote(name));
            } catch (MalformedObjectNameException e1) {
                LOGGER.warn("Unable to register {} {}", type, name, e1);
                throw new RuntimeException(e1);
            }
        }
    }

}
