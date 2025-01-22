package com.jn.agileway.shell.result;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class YamlStyleOutputTransformer implements CmdOutputTransformer{
    @Override
    public String transform(Object methodResult) {
        if(methodResult==null){
            return "";
        }
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setAllowUnicode(true);
        String yaml = new Yaml(dumperOptions).dump(methodResult);
        return yaml;
    }
}