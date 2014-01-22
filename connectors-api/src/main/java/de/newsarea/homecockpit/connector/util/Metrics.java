package de.newsarea.homecockpit.connector.util;

import com.codahale.metrics.MetricRegistry;

public class Metrics {

    private static MetricRegistry metricRegistry;

    public static MetricRegistry getInstance() {
        if(metricRegistry == null) {
            metricRegistry = new MetricRegistry();
        }
        return metricRegistry;
    }

}
