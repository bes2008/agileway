package com.jn.agileway.metrics.supports.jdk.jmx;

import com.jn.agileway.metrics.core.Metric;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * @since 4.1.0
 */
public class DefaultObjectNameFactory implements ObjectNameFactory {

    private static final Logger LOGGER = Loggers.getLogger(JmxReporter.class);

    public ObjectName createName(String type, String domain, Metric metricName) {
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
