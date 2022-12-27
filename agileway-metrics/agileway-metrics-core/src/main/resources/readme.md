# Agileway Metrics vs Dropwizard Metrics


## Meter 对比

| agileway-metrics Meter | dropwizard metrics | usage|
|------------------------|--------------------|------|
| Gauge                  | Gauge              |  瞬时值，可大可小，可为 0 |
| Counter                | Counter            |  计数器，递增方式，可置 0 |
| Histogram              | Histogram          |  直方图，数值维度的统计：可根据多次样本，计算 最大值、最小值、平均值、标准方差、中间值、75%、 95%、 97%、 98%、99%、 99.99% |
| Metered                | Meter              |  基于时间维度统计：可用于统计 1分钟，5分钟，15分钟，平均速率。 例如用于计算 qps |
| Timer                  | Timer              |  结合了 Histogram, Metered ，做了两个维度的统计，可用于计算 历经时间。                     |



# Metric 对比



    