package com.jn.agileway.web.filter.waf.sqlinject;

import com.jn.agileway.web.filter.waf.WAF;
import com.jn.agileway.web.filter.waf.WAFStrategy;
import com.jn.agileway.web.prediates.PathMatchPredicate;
import com.jn.langx.factory.Factory;

public class SqlInjectWafFactory implements Factory<SqlInjectProperties, WAF> {
    @Override
    public WAF get(SqlInjectProperties props) {
        WAF waf = new WAF();
        waf.setName("SQL-Inject-WAF");
        waf.setEnabled(props.isEnabled());

        WAFStrategy strategy = new WAFStrategy();
        strategy.addPredicate(new PathMatchPredicate("/*"));
        strategy.add(new SqlCharRemoveHandler());

        waf.addStrategy(strategy);
        return waf;
    }
}
