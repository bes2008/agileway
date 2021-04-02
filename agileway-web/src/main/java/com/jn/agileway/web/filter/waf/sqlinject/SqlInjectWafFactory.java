package com.jn.agileway.web.filter.waf.sqlinject;

import com.jn.agileway.web.filter.waf.WAFStrategy;
import com.jn.agileway.web.prediates.PathMatchPredicate;
import com.jn.langx.factory.Factory;

public class SqlInjectWafFactory implements Factory<SqlInjectProperties, SqlFirewall> {
    @Override
    public SqlFirewall get(SqlInjectProperties props) {
        SqlFirewall waf = new SqlFirewall();
        waf.setName("SQL-Inject-WAF");
        waf.setConfig(props);

        WAFStrategy strategy = new WAFStrategy();
        strategy.addPredicate(new PathMatchPredicate("/*"));
        strategy.add(new SqlCharRemoveHandler());

        waf.addStrategy(strategy);
        return waf;
    }
}
