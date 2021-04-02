package com.jn.agileway.web.filter.waf.sqlinjection;

import com.jn.agileway.web.filter.waf.WAF;

/**
 * https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html
 * https://owasp.org/www-community/attacks/SQL_Injection
 * https://cheatsheetseries.owasp.org/cheatsheets/Query_Parameterization_Cheat_Sheet.html
 *
 * 对于SQL 注入，
 *
 *
 */
public class SqlFirewall extends WAF {
    private SqlInjectionProperties config;

    public SqlInjectionProperties getConfig() {
        return config;
    }

    public void setConfig(SqlInjectionProperties config) {
        this.config = config;
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        config.setEnabled(enabled);
    }
}
