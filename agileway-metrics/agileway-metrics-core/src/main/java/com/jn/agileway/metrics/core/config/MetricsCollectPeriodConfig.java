package com.jn.agileway.metrics.core.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import static com.jn.agileway.metrics.core.Metric.MetricLevel;

/**
 * 配置不同的Level的Metrics的report时间间隔。
 * 有一个全局的period配置，当某个Level没有具体配置了period，则会取全局的配置。
 *
 * @since 4.1.0
 */
public class MetricsCollectPeriodConfig {

    // 全局interval设保守一点
    public static final int DEFAULT_INTERVAL = 60;

    private Map<MetricLevel, Integer> levelPeriodMap = new HashMap<MetricLevel, Integer>();

    private int globalPeriod = DEFAULT_INTERVAL;

    public MetricsCollectPeriodConfig() {
        fillLevelPeriodMap();
    }

    public MetricsCollectPeriodConfig(int globalPeriodSeconds) {
        this();
        this.configGlobalPeriod(globalPeriodSeconds);
    }

    /**
     * 预先填充map，保证多线程读写map不会有问题
     */
    private void fillLevelPeriodMap() {
        levelPeriodMap.put(MetricLevel.CRITICAL, 1);
        levelPeriodMap.put(MetricLevel.MAJOR, 5);
        levelPeriodMap.put(MetricLevel.NORMAL, 15);
        levelPeriodMap.put(MetricLevel.MINOR, 30);
        levelPeriodMap.put(MetricLevel.TRIVIAL, 60);
    }

    /**
     * 当 seconds < 0 时，会被转为 Integer.MAX_VALUE/10000
     *
     * @param level
     * @param seconds
     * @return
     */
    public MetricsCollectPeriodConfig configPeriod(MetricLevel level, int seconds) {
        if (seconds < 0) {
            seconds = Integer.MAX_VALUE / 10000;
        }
        levelPeriodMap.put(level, seconds);
        return this;
    }

    /**
     * 修改全局的的时间间隔配置，但不会影响到已存在的level的配置。当 globalPeriodSeconds < 0 时，会被转为 Integer.MAX_VALUE/10000
     *
     * @param globalPeriodSeconds
     * @return
     */
    public MetricsCollectPeriodConfig configGlobalPeriod(int globalPeriodSeconds) {
        if (globalPeriodSeconds < 0) {
            globalPeriodSeconds = Integer.MAX_VALUE / 10000;
        }
        this.globalPeriod = globalPeriodSeconds;
        return this;
    }

    public int period(MetricLevel level) {
        Integer value = levelPeriodMap.get(level);
        return value != null ? value : globalPeriod;
    }

    /**
     * 返回最原始的period配置，不受全局的globalPeriod影响
     *
     * @return
     */
    public Map<MetricLevel, Integer> rawLevelPeriodMap() {
        return Collections.unmodifiableMap(levelPeriodMap);
    }

}
