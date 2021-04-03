package com.jn.agileway.web.filter.waf.sqlinjection;

import com.jn.agileway.web.filter.waf.WAFStrategy;
import com.jn.agileway.web.prediate.HttpRequestPredicateGroup;
import com.jn.agileway.web.prediate.HttpRequestPredicateGroupFactory;
import com.jn.langx.factory.Factory;

public class SqlInjectionWafFactory implements Factory<SqlInjectionProperties, SqlFirewall> {
    @Override
    public SqlFirewall get(SqlInjectionProperties props) {
        SqlFirewall waf = new SqlFirewall();
        waf.setName("SQL-Injection-WAF");
        waf.setConfig(props);

        WAFStrategy strategy = new WAFStrategy();

        HttpRequestPredicateGroup predicates = new HttpRequestPredicateGroupFactory().get(props);
        strategy.setPredicates(predicates);
        strategy.add(new SqlCharRemoveHandler());

        waf.addStrategy(strategy);
        return waf;
    }
}
