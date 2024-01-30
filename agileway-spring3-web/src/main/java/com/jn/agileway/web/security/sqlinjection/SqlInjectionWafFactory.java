package com.jn.agileway.web.security.sqlinjection;

import com.jn.agileway.web.security.WAFStrategy;
import com.jn.agileway.web.prediate.HttpRequestPredicateGroup;
import com.jn.agileway.web.prediate.HttpRequestPredicateGroupFactory;
import com.jn.langx.Factory;

public class SqlInjectionWafFactory implements Factory<SqlInjectionProperties, SqlFirewall> {
    @Override
    public SqlFirewall get(SqlInjectionProperties props) {
        SqlFirewall waf = new SqlFirewall();
        waf.setName("SQL-Injection-WAF");
        waf.setConfig(props);

        WAFStrategy strategy = new WAFStrategy();

        HttpRequestPredicateGroup predicates = new HttpRequestPredicateGroupFactory().get(props);
        strategy.setPredicates(predicates);
        strategy.add(new SqlSymbolsHandler());

        waf.addStrategy(strategy);
        return waf;
    }
}
