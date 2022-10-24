## Filters 说明

1. RRFilter 提供对HttpServletRequest, HttpServletResponse 的结对封装，可以在其他地方直接使用
2. AccessLogFilter 提供访问日志
3. GlobalResponseFilter 对相应内容做统一处理，封装成RestRespBody

建议配置顺序：
RRFilter (-103) > AccessLogFilter (-102) > GlobalResponseFilter (-100)  > AllowedMethodsFilter (-99) > CORS (-98)  > XssFilter (-97) >  SQLInjection (-96) >  SetHeaders (-95)> > your custom filter
