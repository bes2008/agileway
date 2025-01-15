package com.jn.agileway.shell.command;

import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import io.github.classgraph.*;
import org.apache.commons.cli.Option;

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

    private CommandGroup resolveCommandClass(ClassInfo classInfo){
        CommandGroup commandGroup = createCommandGroup(classInfo);
        MethodInfoList methodInfoList = classInfo.getDeclaredMethodInfo();
        for (int i = 0; i < methodInfoList.size(); i++) {
            MethodInfo methodInfo = methodInfoList.get(i);
            if(!methodInfo.isPublic() || methodInfo.isAbstract() || methodInfo.isConstructor() || methodInfo.isNative() || methodInfo.isStatic()){
                continue;
            }
            if(!methodInfo.hasAnnotation(com.jn.agileway.shell.command.annotation.Command.class)){
                continue;
            }

        }
        return commandGroup;
    }

    private CommandGroup createCommandGroup(ClassInfo classInfo){
        CommandGroup group = new CommandGroup();
        AnnotationInfo annotationInfo = classInfo.getAnnotationInfo(com.jn.agileway.shell.command.annotation.CommandGroup.class);
        String groupName= null;
        String desc = "";
        if(annotationInfo!=null){
            AnnotationParameterValueList parameterValueList = annotationInfo.getParameterValues(true);
            groupName= (String) parameterValueList.getValue("value");
            desc = (String) parameterValueList.getValue("desc");
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

    private Command createCommand(MethodInfo methodInfo){
        String methodName = methodInfo.getName();
        AnnotationInfo annotationInfo = methodInfo.getAnnotationInfo(com.jn.agileway.shell.command.annotation.Command.class);
        AnnotationParameterValueList parameterValueList = annotationInfo.getParameterValues(true);

        Command command = new Command();
    }

    private Option createOption(){

    }


}
