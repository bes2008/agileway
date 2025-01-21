package com.jn.agileway.shell.builtin;

import com.jn.agileway.shell.command.annotation.Command;
import com.jn.agileway.shell.command.annotation.CommandArgument;
import com.jn.agileway.shell.command.annotation.CommandComponent;
import com.jn.agileway.shell.result.JsonStyleOutputTransformer;
import com.jn.langx.text.properties.Props;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Pipeline;

import java.util.HashMap;
import java.util.Map;

@CommandComponent
public class EnvCommands {

    @Command(value = "env-variables", desc = "Search or list all environment variables ", outputTransformer = JsonStyleOutputTransformer.class)
    public Map<String, String> environmentVariables(
            @CommandArgument(value = "search_text", required = false, desc = "the search text")
            String... search) {

        String[] searchTexts = Pipeline.of(search).clearEmptys().toArray(String[].class);
        if (Objs.isEmpty(searchTexts)) {
            return System.getenv();
        }
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            String envName = entry.getKey();
            String envValue = entry.getValue();
            if (Strings.containsAny(envName, true, searchTexts) || Strings.containsAny(envValue,true, searchTexts )){
                result.put(envName, envValue);
            }
        }
        return result;
    }

    @Command(value = "system-props", desc = "Search or list all system properties", outputTransformer = JsonStyleOutputTransformer.class)
    public Map<String, String> systemProperties(
            @CommandArgument(value = "search_text",required = false,desc = "the search text")
            String... search ){
        String[] searchTexts = Pipeline.of(search).clearEmptys().toArray(String[].class);
        Map<String, String> properties = Props.toStringMap(System.getProperties());

        if(Objs.isEmpty(searchTexts)){
            return properties;
        }

        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String envName = entry.getKey();
            String envValue = entry.getValue();
            if (Strings.containsAny(envName, true, searchTexts ) || Strings.containsAny(envValue, true, searchTexts)) {
                result.put(envName, envValue);
            }
        }
        return result;
    }
}
