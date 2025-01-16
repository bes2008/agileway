package com.jn.agileway.shell.command;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.reflect.Reflects;
import io.github.classgraph.*;
import org.apache.commons.cli.Converter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

public class CommandsScanner  {

    public List<CommandGroup> scan(CommandsScanConfig scanConfig) {
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

        List<CommandGroup> commandGroups = Lists.newArrayList();
        for (int i = 0; i < commandClassInfoList.size(); i++) {
            ClassInfo classInfo = commandClassInfoList.get(i);
            CommandGroup commandGroup = resolveCommandClass(classInfo);
            commandGroups.add(commandGroup);
        }

        return commandGroups;
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
        String optionName;
        String longOptionName=null;

        boolean required = true;
        boolean hasArg1 = true;
        boolean hasArgN = false;
        @Nullable
        String argName = null;
        boolean argOptional = false;
        Class elementType = String.class;
        Class converterClass = DefaultConverter.class;
        String defaultValueString = "";
        char valueSeparator = ',';
        @NonNull
        String desc = "";

        AnnotationParameterValueList parameterValueList = null;
        if(annotationInfo!=null) {
            parameterValueList = annotationInfo.getParameterValues(true);
        }
        if(parameterValueList != null){
            optionName = (String)parameterValueList.getValue("name");
            longOptionName = (String) parameterValueList.getValue("longName");
            required = (boolean)parameterValueList.getValue("required");
            hasArg1 = (boolean)parameterValueList.getValue("hasArg");
            hasArgN=(boolean)parameterValueList.getValue("hasArgs");
            argName=(String)parameterValueList.getValue("argName");
            argOptional=(boolean)parameterValueList.getValue("argOptional");
            elementType=(Class)parameterValueList.getValue("type");
            converterClass = (Class) parameterValueList.getValue("converter");
            defaultValueString=(String) parameterValueList.getValue("defaultValue");
            valueSeparator = (char)parameterValueList.getValue("valueSeparator");
            desc = (String)parameterValueList.getValue("desc");
        }else{
            optionName = methodParameterInfo.getName();
            if(methodParameterInfo.getAnnotationInfo(Nullable.class)==null){
                required = false;
                argOptional=true;
            }
            Parameter parameter = method.getParameters()[parameterIndex];
            Class parameterClass = parameter.getType();
            if(parameterClass.isArray() || Reflects.isSubClassOrEquals(Collection.class, parameterClass)){
                hasArgN=true;
                if(parameterClass.isArray()){
                    elementType = parameterClass.getComponentType();
                }else{
                    ParameterizedType parameterizedType =(ParameterizedType)parameter.getParameterizedType();
                    elementType = (Class)parameterizedType.getActualTypeArguments()[0];
                }
            }else {
                elementType = parameterClass;
            }
            argName = parameter.getName();
        }

        final Converter converter = converterClass==null? new DefaultConverter(elementType) : Reflects.<Converter>newInstance(converterClass);
        List<Object> defaultValues = null;
        if(hasArgN){
            if(defaultValueString!=null){

                    String[] values = Strings.split(defaultValueString, valueSeparator + "");
                    defaultValues = Pipeline.of(values).map(new Function<String, Object>() {
                        @Override
                        public Object apply(String value) {
                            try {
                                return converter.apply(value);
                            }catch (Throwable e){
                                throw new RuntimeException(e);
                            }
                        }
                    }).asList();

            }
        }else  if(hasArg1 && defaultValueString!=null){
            try {
                Object defaultValue = converter.apply(defaultValueString);
                defaultValues=Lists.newArrayList(defaultValue);
            }catch (Throwable e){
                throw new RuntimeException(e);
            }
        }

        try {
            CommandOption option = new CommandOption(optionName, longOptionName, hasArg1, desc);
            option.setOptionalArg(argOptional);
            option.setArgName(argName);
            option.setRequired(required);
            if(hasArgN) {
                option.setArgs( Option.UNLIMITED_VALUES );
            }
            option.setType(elementType);
            option.setConverter(converter);
            if(hasArgN || hasArg1){
                option.setDefaultValues(defaultValues);
            }
            option.setDefaultValue(valueSeparator);
            return option;
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
    }

}
