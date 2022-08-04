# dropwizard metrics

## 度量类型

+ Gauge：用于提供指标的瞬时值
+ Counter：计数器，本质是一个java.util.concurrent.atomic.LongAdder， 用于对指标的值进行加、减计数
+ Histogram：直方图数据，代表了一个指标（数据流类的指标）的分布情况（比例情况）。 最小值、最大值、平均值、中位数、p75、p90、p95、p98、p99、p999数据
+ Meter：统计系统中某一事件的响应速率，如TPS、QPS。该项指标值直接反应系统当前的处理能力
+ Timer：计时器，是Meter和Histogram的结合，可以统计接口请求速率和响应时长。

