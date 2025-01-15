package com.jn.agileway.shell.command;

import com.jn.langx.util.collection.Collects;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.util.List;

public class CommandsScanner  {

    public List<Command> scan(CommandsScanConfig scanConfig) {
        ScanResult scanResult=new ClassGraph()
                .enableClassInfo()
                .enableMethodInfo()
                .enableAnnotationInfo()
                .addClassLoader(this.getClass().getClassLoader())
                .acceptPackages(Collects.toArray(scanConfig.getPackages(), String[].class)).scan();
        ClassInfoList classInfoList = scanResult.getClassesWithAnyAnnotation(
                com.jn.agileway.shell.command.annotation.CommandGroup.class,
                com.jn.agileway.shell.command.annotation.Command.class,
                com.jn.agileway.shell.command.annotation.CommandGroup.class);
        return null;
    }
}
