package com.jn.agileway.metrics.core.meter;

import com.jn.agileway.metrics.core.Meter;

import java.util.Map;

/**
 * 用于在高吞吐率环境下，快速统计方法的qps，rt，成功率和错误码。
 * 保证最多只进行一次 com.jn.agileway.metrics.core.LongAdder#add() 计数操作
 * 典型的应用场景，中间件的tair客户端
 *
 * @since 4.1.0
 */
public interface FastCompass extends Meter {

    /**
     * 记录一次方法调用的RT和子类别，子类别应当是正交的，不能有重叠
     * 例如 成功/失败
     * record a method invocation with execution time and sub-categories
     *
     * @param duration    must be milliseconds
     * @param subCategory all the sub-categories should be orthogonal,
     *                    which will be added up to the total number of method invocations
     */
    void record(long duration, String subCategory);

    /**
     * 对于每个子类别，返回每个统计间隔的方法调用总次数
     * return method count per bucket per category
     *
     * @return
     */
    Map<String, Map<Long, Long>> getMethodCountPerCategory();


    /**
     * 对于每个子类别，返回每个统计间隔的方法调用总次数
     * return method count per bucket per category
     *
     * @return
     */
    Map<String, Map<Long, Long>> getMethodCountPerCategory(long startTime);

    /**
     * 对于每个子类别，返回每个统计间隔的方法执行总时间
     * return method execution time per bucket per category
     *
     * @return
     */
    Map<String, Map<Long, Long>> getMethodRtPerCategory();

    /**
     * 对于每个子类别，返回每个统计间隔的方法执行总时间
     * return method execution time per bucket per category
     *
     * @return
     */
    Map<String, Map<Long, Long>> getMethodRtPerCategory(long startTime);

    /**
     * 对于每个子类别，返回每个统计间隔的执行总时间和次数，按位分离操作放到下一层进行
     * return method execution time and count per bucket per category
     *
     * @return
     */
    Map<String, Map<Long, Long>> getCountAndRtPerCategory();

    /**
     * 对于每个子类别，返回每个统计间隔的执行总时间和次数，按位分离操作放到下一层进行
     * return method execution time and count per bucket per category
     *
     * @return
     */
    Map<String, Map<Long, Long>> getCountAndRtPerCategory(long startTime);

    /**
     * 获取统计间隔
     *
     * @return the bucket interval
     */
    int getBucketInterval();
}
