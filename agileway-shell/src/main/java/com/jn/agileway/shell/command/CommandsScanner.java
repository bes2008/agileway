package com.jn.agileway.shell.command;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Lists;
import io.github.classgraph.*;
import org.apache.commons.cli.Converter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.lang.reflect.Method;
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
            CommandGroup commandGroup = resolveCommandClass(classInfo);
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
            Command command = createCommand(methodInfo);
            commandGroup.addCommand(command);
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
        AnnotationInfo annotationInfo = methodInfo.getAnnotationInfo(com.jn.agileway.shell.command.annotation.Command.class);
        AnnotationParameterValueList parameterValueList = annotationInfo.getParameterValues(true);

        String name = null;
        String[] alias=null;
        String desc =null;
        if (parameterValueList!=null){
            name = (String)parameterValueList.getValue("name");
            alias = (String[]) parameterValueList.getValue("alias");
            desc = (String)parameterValueList.getValue("desc");
        }

        if(Strings.isBlank(name)){
            name =  methodInfo.getName();
        }

        if(desc==null){
            desc = "";
        }
        Command command = new Command();
        command.setAlias(Lists.newArrayList(alias));
        command.setName(name);
        Method method = methodInfo.loadClassAndGetMethod();
        command.setMethod(method);
        command.setDesc(desc);

        Options options = new Options();
        MethodParameterInfo[] methodParameterInfoList = methodInfo.getParameterInfo();
        for (int i = 0; i < methodParameterInfoList.length ; i++) {
            MethodParameterInfo methodParameterInfo = methodParameterInfoList[i];
            Option option = createOption(methodParameterInfo, method, i);
            options.addOption(option);
        }

        command.setOptions(options);
        return command;
    }

    private CommandOption createOption(MethodParameterInfo methodParameterInfo, Method method, int parameterIndex){
        AnnotationInfo annotationInfo = methodParameterInfo.getAnnotationInfo(com.jn.agileway.shell.command.annotation.CommandOption.class);

        @Nullable
        String optionName=null;
        String longOptionName=null;
        boolean required = true;
        boolean hasArg = true;
        boolean hasArgs = false;
        @Nullable
        String argName = null;
        boolean argOptional = false;
        Class type = String.class;
        Class converterClass= Converter.class;
        String defaultValue = "";
        String[] defaultValues = new String[0];
        char valueSeparator = ',';
        @NonNull
        String desc = "";

        if(annotationInfo!=null){

        }else{

        }

        try {
            CommandOption option = new CommandOption(optionName, longOptionName, hasArg, desc);
            option.setOptionalArg(argOptional);
            option.setArgName(argName);
            option.setRequired(required);
            option.setArgs(hasArgs?Option.UNLIMITED_VALUES:(hasArg?1:0));
            option.setType(type);

            if(hasArgs){

            }
            return option;
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
    }

}
