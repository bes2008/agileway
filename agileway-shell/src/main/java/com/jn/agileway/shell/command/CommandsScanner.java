package com.jn.agileway.shell.command;

import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import io.github.classgraph.*;

import java.util.List;

public class CommandsScanner  {

    public List<Command> scan(CommandsScanConfig scanConfig) {
        ScanResult scanResult=new ClassGraph()
                .enableClassInfo()
                .enableMethodInfo()
                .enableAnnotationInfo()
                .addClassLoader(this.getClass().getClassLoader())
                .acceptPackages(Collects.toArray(scanConfig.getPackages(), String[].class)).scan();
        ClassInfoList commandClassInfoList = scanResult.getClassesWithAnnotation(com.jn.agileway.shell.command.annotation.Command.class);
        commandClassInfoList = commandClassInfoList.filter(new ClassInfoList.ClassInfoFilter() {
            @Override
            public boolean accept(ClassInfo classInfo) {
                if(classInfo.isAnnotation() || classInfo.isInterface()
                        || classInfo.isArrayClass() || classInfo.isEnum() || classInfo.isRecord()
                        || classInfo.isPrivate() || classInfo.isProtected()
                        || classInfo.isInnerClass()
                        || classInfo.isStatic()
                        || classInfo.isStandardClass()
                ) {
                    return false;
                }
                return true;
            }
        });

        for (int i = 0; i < commandClassInfoList.size(); i++) {
            ClassInfo classInfo = commandClassInfoList.get(i);
        }

        return null;
    }

    private CommandGroup buildCommandClass(ClassInfo classInfo){
        CommandGroup commandGroup = createCommandGroup(classInfo);

        return commandGroup;
    }

    private CommandGroup createCommandGroup(ClassInfo classInfo){
        CommandGroup group = new CommandGroup();
        AnnotationInfo annotationInfo = classInfo.getAnnotationInfo(com.jn.agileway.shell.command.annotation.CommandGroup.class);
        String groupName= null;
        String desc = "";
        if(annotationInfo!=null){
            AnnotationParameterValueList valueList = annotationInfo.getParameterValues(true);
            groupName= (String) valueList.getValue("value");
            desc = (String) valueList.getValue("desc");
        }
        if(Strings.isBlank(groupName)){
            groupName= classInfo.getSimpleName();
        }
        if(desc==null){
            desc = "";
        }
        group.setDesc(desc);
        group.setName(groupName);

        return group;
    }



}
