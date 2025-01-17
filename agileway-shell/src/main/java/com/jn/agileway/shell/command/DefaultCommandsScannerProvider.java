package com.jn.agileway.shell.command;

import com.jn.langx.Provider;
import com.jn.langx.environment.Environment;
import com.jn.langx.util.Booleans;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Pipeline;

public class DefaultCommandsScannerProvider implements Provider<Environment, DefaultCommandsScanner> {
    private static final String SCANNER_ENABLED_PROP = "agileway.shell.scanner.default.enabled";
    private static final String SCANNER_PACKAGES_PROP = "agileway.shell.scanner.default.packages";
    @Override
    public DefaultCommandsScanner get(Environment env) {
        boolean defaultScannerEnabled = Booleans.truth( env.getProperty(SCANNER_ENABLED_PROP, "true"));
        String scanPackages = env.getProperty(SCANNER_PACKAGES_PROP);
        CommandsScanConfig commandsScanConfig = new CommandsScanConfig();
        commandsScanConfig.setEnabled(defaultScannerEnabled);
        commandsScanConfig.setPackages(Pipeline.of(Strings.split(scanPackages,",")).asList());
        return new DefaultCommandsScanner(commandsScanConfig);
    }
}
