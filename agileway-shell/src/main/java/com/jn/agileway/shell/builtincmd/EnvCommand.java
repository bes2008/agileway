package com.jn.agileway.shell.builtincmd;

import com.jn.agileway.shell.command.annotation.Command;
import com.jn.agileway.shell.command.annotation.CommandComponent;
import com.jn.agileway.shell.command.annotation.CommandOption;
import com.jn.langx.text.properties.Props;
import com.jn.langx.util.Strings;
import com.jn.langx.util.SystemPropertys;
import com.jn.langx.util.collection.Collects;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@CommandComponent

public class EnvCommand {
    @Command("env-variables")
    public Map<String, String> environmentVariables(
            @CommandOption(value = "s", longName = "search", required = false)
            String search) {

        search = Strings.trimToNull(search);
        if (search == null) {
            return System.getenv();
        }
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            String envName = entry.getKey();
            String envValue = entry.getValue();
            if (Strings.contains(envName, search, true) || Strings.contains(envValue, search, true)){
                result.put(envName, envValue);
            }
        }
        return result;
    }

    @Command("system-props")
    public Map<String, String> systemProperties(
            @CommandOption(value = "s", longName = "search", required = false)
            String search ){
        search = Strings.trimToNull(search);
        Map<String, String> properties = Props.toStringMap(System.getProperties());

        if(search==null){
            return properties;
        }

        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String envName = entry.getKey();
            String envValue = entry.getValue();
            if (Strings.contains(envName, search, true) || Strings.contains(envValue, search, true)) {
                result.put(envName, envValue);
            }
        }
        return result;
    }
}
