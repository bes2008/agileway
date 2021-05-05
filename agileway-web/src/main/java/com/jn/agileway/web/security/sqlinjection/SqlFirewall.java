package com.jn.agileway.web.security.sqlinjection;

import com.jn.agileway.web.security.WAF;

/**
 * https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html
 * https://owasp.org/www-community/attacks/SQL_Injection
 * https://cheatsheetseries.owasp.org/cheatsheets/Query_Parameterization_Cheat_Sheet.html
 * <p>
 * 对于SQL 注入， 主要的防御手段有：
 *
 * <pre>
 *     1. 访问数据库时，使用 Prepared Statements，避免使用 字符串拼接方式的动态SQL
 *     2. 访问数据库时，使用 Stored Procedures
 *     3. 对用户输入内容进行 特殊字符验证
 *     4. 对用户输入的内容 进行Escaping
 *     5. 访问数据库时，需要进行密码校验，不能裸奔
 *
 *     由于1,2 是数据库访问层的做法，在这一层要做的是提供 3,4 的支持
 * </pre>
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
