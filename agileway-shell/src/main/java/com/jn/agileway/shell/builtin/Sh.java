package com.jn.agileway.shell.builtin;

import com.jn.agileway.shell.command.annotation.Command;
import com.jn.agileway.shell.command.annotation.CommandArgument;
import com.jn.agileway.shell.command.annotation.CommandOption;
import com.jn.agileway.shell.exception.ShellInterruptedException;
import com.jn.agileway.shell.result.YamlStyleOutputTransformer;
import com.jn.langx.text.properties.Props;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Pipeline;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.util.HashMap;
import java.util.Map;

public class Sh {
    @Command(value = "clear", alias = {"cls","clean"}, desc = "clear screen")
    public void clear(){
        if (AnsiConsole.isInstalled()) {
            System.out.println(Ansi.ansi().eraseScreen().cursor(1, 1));
        }
    }

    @Command(value = "quit", alias = {"exit"})
    public void quit(){
        throw new ShellInterruptedException("quit", 0);
    }


    @Command(value = "env-variables", desc = "Search or list all environment variables ", outputTransformer = YamlStyleOutputTransformer.class)
    public Map<String, String> environmentVariables(
            @CommandOption(value = "i", longName = "ignoreCase", isFlag = true, desc = "search string with ignore case")
            boolean ignoreCase,
            @CommandOption(value = "v", longName = "searchValue", isFlag = true, desc = "search variable value also")
            boolean searchValue,
            @CommandArgument(value = "search_text", desc = "the search text")
            String... search) {

        String[] searchTexts = Pipeline.of(search).clearEmptys().toArray(String[].class);
        if (Objs.isEmpty(searchTexts)) {
            return System.getenv();
        }
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            String envName = entry.getKey();
            String envValue = entry.getValue();
            if (Strings.containsAny(envName, ignoreCase, searchTexts)) {
                result.put(envName, envValue);
            }
            if (searchValue && Strings.containsAny(envValue, ignoreCase, searchTexts)) {
                result.put(envName, envValue);
            }
        }
        return result;
    }

    @Command(value = "system-props", desc = "Search or list all system properties", outputTransformer = YamlStyleOutputTransformer.class)
    public Map<String, String> systemProperties(
            @CommandOption(value = "i", longName = "ignoreCase", isFlag = true, desc = "search string with ignore case")
            boolean ignoreCase,
            @CommandOption(value = "v", longName = "searchValue", isFlag = true, defaultValue = "true", desc = "search property value also")
            boolean searchValue,
            @CommandArgument(value = "search_text", desc = "the search text")
            String... search) {
        String[] searchTexts = Pipeline.of(search).clearEmptys().toArray(String[].class);
        Map<String, String> properties = Props.toStringMap(System.getProperties());

        if (Objs.isEmpty(searchTexts)) {
            return properties;
        }

        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String envName = entry.getKey();
            String envValue = entry.getValue();
            if (Strings.containsAny(envName, ignoreCase, searchTexts)) {
                result.put(envName, envValue);
            }
            if (searchValue && Strings.containsAny(envValue, ignoreCase, searchTexts)) {
                result.put(envName, envValue);
            }
        }
        return result;
    }
}
