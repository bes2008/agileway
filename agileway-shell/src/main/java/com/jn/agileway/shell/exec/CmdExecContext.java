package com.jn.agileway.shell.exec;

import com.jn.langx.environment.Environment;
import com.jn.langx.util.converter.ConverterService;

public class CmdExecContext {
    private Environment env;
    private ConverterService converterService;
    private CommandComponentFactory componentFactory;

    public Environment getEnv() {
        return env;
    }

    public void setEnv(Environment env) {
        this.env = env;
    }

    public ConverterService getConverterService() {
        return converterService;
    }

    public void setConverterService(ConverterService converterService) {
        this.converterService = converterService;
    }

    public CommandComponentFactory getComponentFactory() {
        return componentFactory;
    }

    public void setComponentFactory(CommandComponentFactory componentFactory) {
        this.componentFactory = componentFactory;
    }
}
