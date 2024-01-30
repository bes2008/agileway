## Filters 说明

1. RRFilter 提供对HttpServletRequest, HttpServletResponse 的结对封装，可以在其他地方直接使用
2. AccessLogFilter 提供访问日志
3. GlobalResponseFilter 对相应内容做统一处理，封装成RestRespBody

建议配置顺序：
RRFilter (-1030) > AccessLogFilter (-1020) > GlobalResponseFilter (-1000)  > AllowedMethodsFilter (-990) > CORS (-980)  > XssFilter (-970) >  SQLInjection (-960) >  SetHeaders (-950)> > your custom filter
