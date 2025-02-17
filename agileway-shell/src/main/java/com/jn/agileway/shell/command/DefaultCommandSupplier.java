package com.jn.agileway.shell.command;

import com.jn.agileway.shell.command.annotation.CommandComponent;
import com.jn.agileway.shell.exception.MalformedCommandException;
import com.jn.agileway.shell.result.CmdOutputTransformer;
import com.jn.agileway.shell.result.RawTextOutputTransformer;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.environment.Environment;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Booleans;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Modifiers;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.reflect.type.Primitives;
import com.jn.langx.util.struct.Pair;
import io.github.classgraph.*;
import org.apache.commons.cli.Converter;
import org.apache.commons.cli.Option;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DefaultCommandSupplier implements CommandSupplier {
    private static final String SCAN_ENABLED_PROP = "agileway.shell.scan.enabled";
    private static final String SCAN_PACKAGES_PROP = "agileway.shell.scan.packages";
    private static final String OPTION_SHORT_NAME_SINGLE_CHAR_PROP="agileway.shell.option.shortname.singleChar";

    private static final String SCAN_BUILTIN_PACKAGES = "agileway.shell.scan.builtin.enabled";
    private static final String BUILTIN_PACKAGE = "com.jn.agileway.shell.builtin";

    public CommandScanConfig buildScanConfig(Environment env) {
        boolean defaultScannerEnabled = Booleans.truth(env.getProperty(SCAN_ENABLED_PROP, "true"));
        String scanPackages = env.getProperty(SCAN_PACKAGES_PROP);
        boolean scanBuiltinPackage = Booleans.truth(env.getProperty(SCAN_BUILTIN_PACKAGES, "true"));
        CommandScanConfig commandsScanConfig = new CommandScanConfig();
        commandsScanConfig.setEnabled(defaultScannerEnabled);
        commandsScanConfig.setBuiltinPackagesEnabled(scanBuiltinPackage);

        List<String> scanPackageList = Pipeline.of(Strings.split(scanPackages, ",")).addIf(BUILTIN_PACKAGE, new Predicate2<Collection<String>, String>() {
            @Override
            public boolean test(Collection<String> packages, String string) {
                return scanBuiltinPackage;
            }
        }).distinct().asList();

        if (defaultScannerEnabled && Objs.isEmpty(scanPackageList)) {
            Loggers.getLogger(DefaultCommandSupplier.class).warn("property is empty: {}", SCAN_PACKAGES_PROP);
        }
        commandsScanConfig.setPackages(scanPackageList);
        return commandsScanConfig;
    }

    @Override
    public Map<CommandGroup, List<Command>> get(Environment env) {
        return scan(buildScanConfig(env), env);
    }

    private Map<CommandGroup, List<Command>> scan(CommandScanConfig scanConfig,Environment env) {
        Map<CommandGroup, List<Command>> result = new HashMap<>();

        if (!scanConfig.isEnabled() || Objs.isEmpty(scanConfig.getPackages())) {
            return result;
        }

        ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .enableMethodInfo()
                .enableAnnotationInfo()
                .addClassLoader(this.getClass().getClassLoader())
                .acceptPackages(Collects.toArray(scanConfig.getPackages(), String[].class)).scan();
        ClassInfoList commandClassInfoList = scanResult.getClassesWithAnnotation(CommandComponent.class);
        commandClassInfoList = commandClassInfoList.filter(new ClassInfoList.ClassInfoFilter() {
            @Override
            public boolean accept(ClassInfo classInfo) {
                if (classInfo.isAnnotation() || classInfo.isInterface()
                        || classInfo.isArrayClass() || classInfo.isEnum() || classInfo.isRecord()
                        || classInfo.isPrivate() || classInfo.isProtected()
                        || classInfo.isInnerClass()
                        || classInfo.isStatic()
                        || !classInfo.isStandardClass()
                ) {
                    return false;
                }
                return true;
            }
        });


        for (int i = 0; i < commandClassInfoList.size(); i++) {
            ClassInfo classInfo = commandClassInfoList.get(i);
            Pair<CommandGroup, List<Command>> groupCommandsEntry = resolveCommandClass(classInfo, env);
            result.put(groupCommandsEntry.getKey(), groupCommandsEntry.getValue());
        }

        return result;
    }

    private Pair<CommandGroup, List<Command>> resolveCommandClass(ClassInfo classInfo, Environment env) {
        classInfo.getPackageInfo();
        CommandGroup commandGroup = createCommandGroup(classInfo);

        Class commandComponentClass = classInfo.loadClass();

        List<Command> commands = Lists.newArrayList();

        Pair<CommandGroup, List<Command>> result = new Pair<>(commandGroup, commands);

        Map<String, CommandAvailability> availabilityMap = Maps.<String, CommandAvailability>newMap();

        // 获取到类以及父类中，所有的public方法（包含继承 ）
        Method[] methods = commandComponentClass.getMethods();

        // 解析 CommandAvailability列表
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if(!Modifiers.isPublic(method) || Modifiers.isAbstract(method) || Modifiers.isSynthetic(method) || Modifiers.isNative(method) || Modifiers.isStatic(method)){
                continue;
            }
            if(method.getParameterCount()>0){
                continue;
            }
            if (Reflects.hasAnnotation(method, com.jn.agileway.shell.command.annotation.Command.class)) {
                continue;
            }
            if(method.getReturnType()!= Availability.class){
                continue;
            }

            CommandAvailability commandAvailability = null;
            if(Reflects.hasAnnotation(method, com.jn.agileway.shell.command.annotation.CommandAvailability.class)){
                com.jn.agileway.shell.command.annotation.CommandAvailability availabilityAnnotation = Reflects.getAnnotation(method, com.jn.agileway.shell.command.annotation.CommandAvailability.class);
                commandAvailability = new CommandAvailability(method, availabilityAnnotation.value());
            }else{
                commandAvailability = new CommandAvailability(method, new String[]{});
            }
            availabilityMap.put(commandAvailability.getName(), commandAvailability);
        }

        MethodInfoList methodInfoList = classInfo.getDeclaredMethodInfo();
        // 解析命令定义
        for (int i = 0; i < methodInfoList.size(); i++) {
            MethodInfo methodInfo = methodInfoList.get(i);
            if (!methodInfo.isPublic() || methodInfo.isAbstract() || methodInfo.isConstructor() || methodInfo.isSynthetic() || methodInfo.isNative() || methodInfo.isStatic()) {
                continue;
            }
            if (!methodInfo.hasAnnotation(com.jn.agileway.shell.command.annotation.Command.class)) {
                continue;
            }
            Command command = createCommand(methodInfo, availabilityMap, env);
            command.setGroup(commandGroup.getName());
            commands.add(command);
        }
        return result;
    }

    /**
     * 创建 commandGroup, 优先使用 class上的 @ComponentGroup，如果没有就使用 package上的 @ComponentGroup
     *
     * @param classInfo class info
     * @return command group
     */
    private CommandGroup createCommandGroup(ClassInfo classInfo) {
        CommandGroup group = new CommandGroup();
        AnnotationInfo annotationInfo = classInfo.getAnnotationInfo(com.jn.agileway.shell.command.annotation.CommandGroup.class);
        String groupName = null;
        String desc = "";
        Class outputTransformerClass = null;
        boolean packageAnnotationed = false;
        if (annotationInfo == null) {
            annotationInfo = classInfo.getPackageInfo().getAnnotationInfo(com.jn.agileway.shell.command.annotation.CommandGroup.class);
            packageAnnotationed = true;
        }
        if (annotationInfo != null) {
            AnnotationParameterValueList parameterValueList = annotationInfo.getParameterValues(true);
            groupName = (String) parameterValueList.getValue("value");
            desc = (String) parameterValueList.getValue("desc");
            AnnotationClassRef outputTransformerClassRef = ((AnnotationClassRef) parameterValueList.getValue("outputTransformer"));
            if (outputTransformerClassRef != null) {
                outputTransformerClass = outputTransformerClassRef.loadClass();
            }
        }

        if (outputTransformerClass == null && packageAnnotationed) {
            outputTransformerClass = RawTextOutputTransformer.class;
        }
        if (Strings.isBlank(groupName)) {
            groupName = classInfo.getSimpleName();
        }
        if (Objs.equals(groupName, CommandGroup.BUILTIN_GROUP) && !Strings.equals(classInfo.getPackageInfo().getName(), BUILTIN_PACKAGE)) {
            throw new MalformedCommandException("customized command use the 'builtin' command group");
        }
        if (desc == null) {
            desc = "";
        }
        group.setDesc(desc);
        group.setName(groupName);

        if (outputTransformerClass != null) {
            CmdOutputTransformer transformer = Reflects.<CmdOutputTransformer>newInstance(outputTransformerClass);
            group.setOutputTransformer(transformer);
        }

        return group;
    }

    private Command createCommand(MethodInfo methodInfo,Map<String, CommandAvailability> availabilityMap, Environment env) {
        AnnotationInfo annotationInfo = methodInfo.getAnnotationInfo(com.jn.agileway.shell.command.annotation.Command.class);
        AnnotationParameterValueList parameterValueList = annotationInfo.getParameterValues(true);

        String name = null;
        String[] alias = null;
        String desc = null;
        Class outputTransformerClass = null;
        if (parameterValueList != null) {
            name = (String) parameterValueList.getValue("value");
            alias = (String[]) parameterValueList.getValue("alias");
            desc = (String) parameterValueList.getValue("desc");
            AnnotationClassRef outputTransformerClassRef = ((AnnotationClassRef) parameterValueList.getValue("outputTransformer"));
            if (outputTransformerClassRef != null) {
                outputTransformerClass = outputTransformerClassRef.loadClass();
            }
        }

        if (Strings.isBlank(name)) {
            name = methodInfo.getName();
        }

        desc = Objs.useValueIfEmpty(desc, name);

        Command command = new Command();
        command.setAlias(Lists.newArrayList(alias));
        command.setName(name);
        Method method = methodInfo.loadClassAndGetMethod();
        command.setMethod(method);
        command.setDesc(desc);
        CmdOutputTransformer transformer = null;
        if (outputTransformerClass != null) {
            transformer = Reflects.<CmdOutputTransformer>newInstance(outputTransformerClass);
        }
        command.setOutputTransformer(transformer);

        MethodParameterInfo[] methodParameterInfoList = methodInfo.getParameterInfo();
        List<Option> options = Lists.newArrayListWithCapacity(methodParameterInfoList.length);

        int firstCommandArgumentIndex = Collects.<MethodParameterInfo, Collection<MethodParameterInfo>>firstOccurrence(Collects.<MethodParameterInfo>asList(methodParameterInfoList), new Predicate2<Integer, MethodParameterInfo>() {
            @Override
            public boolean test(Integer idx, MethodParameterInfo methodParameterInfo) {
                AnnotationInfo commandArgumentAnnotationInfo = methodParameterInfo.getAnnotationInfo(com.jn.agileway.shell.command.annotation.CommandArgument.class);
                return commandArgumentAnnotationInfo != null;
            }
        });
        List<CommandArgument> arguments = Lists.newArrayList();
        for (int i = 0; i < methodParameterInfoList.length; i++) {
            MethodParameterInfo methodParameterInfo = methodParameterInfoList[i];
            if (firstCommandArgumentIndex < 0 || i < firstCommandArgumentIndex) {
                Option option = createCommandOption(env, command.getName(), methodParameterInfo, method, i);
                options.add(option);
            } else {
                CommandArgument commandArgument = createCommandArgument(env,command.getName(), methodParameterInfo, method, i);
                arguments.add(commandArgument);
            }
        }

        command.setOptions(options);

        // 对参数进行校验
        int lastRequiredIndex = Collects.lastIndexOf(arguments, new Predicate<CommandArgument>() {
            @Override
            public boolean test(CommandArgument commandArgument) {
                return commandArgument.isRequired();
            }
        });
        // 判定 在 lastRequiredIndex 之前的 argument 都是 required
        for (int i = 0; i < lastRequiredIndex; i++) {
            if (!arguments.get(i).isRequired()) {
                throw new MalformedCommandException(StringTemplates.formatWithPlaceholder("@CommandArgument required() in [{}th, {}th] should be true for method {}", options.size(), lastRequiredIndex, Reflects.getFQNClassName(method.getDeclaringClass()) + "#" + method.getName()));
            }
        }
        command.setArguments(arguments);


        if(Reflects.hasAnnotation(method, com.jn.agileway.shell.command.annotation.CommandAvailability.class)){
            com.jn.agileway.shell.command.annotation.CommandAvailability availabilityAnnotation = Reflects.getAnnotation(method, com.jn.agileway.shell.command.annotation.CommandAvailability.class);
            String[] expectedAvailabilityList = availabilityAnnotation.value();
            for (int i = 0; i < expectedAvailabilityList.length; i++){
                String expectedAvailabilityName = expectedAvailabilityList[i];
                if (Strings.isBlank(expectedAvailabilityName)) {
                    continue;
                }
                if (availabilityMap.containsKey(expectedAvailabilityName)) {
                    CommandAvailability commandAvailability = availabilityMap.get(expectedAvailabilityName);
                    if (commandAvailability.isAvailableFor(command)) {
                        command.addAvailability(commandAvailability);
                    }
                }
                throw new MalformedCommandException(StringTemplates.formatWithPlaceholder("CommandAvailability {} is not available for command {}", expectedAvailabilityName, command.getName()));
            }
        }
        else{
            for (Map.Entry<String, CommandAvailability> entry : availabilityMap.entrySet()){
                if(entry.getValue().isAvailableFor(command)){
                    command.addAvailability(entry.getValue());
                }
            }
        }

        return command;
    }

    private CommandArgument createCommandArgument(Environment env, String commandKey, MethodParameterInfo methodParameterInfo, Method method, int parameterIndex) {
        AnnotationInfo annotationInfo = methodParameterInfo.getAnnotationInfo(com.jn.agileway.shell.command.annotation.CommandArgument.class);
        if (annotationInfo == null) {
            throw new MalformedCommandException(StringTemplates.formatWithPlaceholder("Missing @CommandArgument at the {}th parameter for method {}", parameterIndex, Reflects.getFQNClassName(method.getDeclaringClass()) + "#" + method.getName()));
        }

        boolean isMultipleValue = false;
        Parameter parameter =method.getParameters()[parameterIndex];
        Class parameterType = parameter.getType();

        if (parameterIndex < method.getParameters().length - 1) {
            // 非最后一个参数，类型只能是 String
            if (!Commands.isSupportedBasicTypes(parameterType)) {
                throw new IllegalArgumentException(StringTemplates.formatWithPlaceholder("parameter type should be basic types ({}) at {}th for method {}", Commands.getSupportedBaseTypesString(), parameterIndex+1, Reflects.getFQNClassName(method.getDeclaringClass()) + "#" + method.getName()));
            }
        } else if (parameterIndex == method.getParameters().length - 1) {
            // 最后一个参数，类型可以是 String 或者 String[]
            if (parameterType.isArray() && Commands.isSupportedBasicTypes(parameterType.getComponentType())) {
                isMultipleValue = true;
            }else if (!Commands.isSupportedBasicTypes(parameterType)){
                throw new IllegalArgumentException(StringTemplates.formatWithPlaceholder("parameter type should be basic types ({}) or base types array at {}th for method {}", Commands.getSupportedBaseTypesString(), parameterIndex+1, Reflects.getFQNClassName(method.getDeclaringClass()) + "#" + method.getName()));
            }
        }
        AnnotationParameterValueList parameterValueList = annotationInfo.getParameterValues(true);
        String name = (String) parameterValueList.getValue("value");
        String desc = (String) parameterValueList.getValue("desc");
        Class converterClass = ((AnnotationClassRef) parameterValueList.getValue("converter")).loadClass();
        String defaultValueString = (String) parameterValueList.getValue("defaultValue").toString();

        if (Strings.isBlank(name)) {
            throw new MalformedCommandException(StringTemplates.formatWithPlaceholder("@CommandArgument value() at the {}th parameter is required for method {}", parameterIndex+1, Reflects.getFQNClassName(method.getDeclaringClass()) + "#" + method.getName()));
        }
        desc = Objs.useValueIfEmpty(desc, name);

        Class elementType = Commands.getConverterTargetClass(parameter, false);
        Converter converter = Commands.newConverter(converterClass, elementType);


        boolean required = Commands.isRequiredCommandArgument(method, parameterIndex, defaultValueString);

        String defaultValue = null;
        String[] defaultValues = null;
        if(!Commands.isNullDefaultValue(defaultValueString) && parameterIndex == method.getParameterCount()-1){
            if(parameterType.isArray()){
                String[] values= Strings.split(defaultValueString," ");
                Commands.checkOptionOrArgumentDefaultValues(values, converter, name, commandKey);
                defaultValues = values;
            }else{
                Commands.checkOptionOrArgumentDefaultValue(defaultValueString, converter, name, commandKey);
                defaultValue = defaultValueString;
            }
        }
        CommandArgument argument = new CommandArgument();
        argument.setRequired(required);
        argument.setName(name);
        argument.setDesc(desc);
        argument.setMultipleValue(isMultipleValue);
        argument.setType(elementType);
        argument.setConverter(converter);
        argument.setDefaultValue(defaultValue);
        argument.setDefaultValues(defaultValues);
        return argument;
    }

    private CommandOption createCommandOption(Environment env, final String commandKey, MethodParameterInfo methodParameterInfo, Method method, int parameterIndex) {
        AnnotationInfo annotationInfo = methodParameterInfo.getAnnotationInfo(com.jn.agileway.shell.command.annotation.CommandOption.class);
        if(annotationInfo==null){
            throw new MalformedCommandException(StringTemplates.formatWithPlaceholder("Missing @CommandOption at the {}th parameter for method {}", parameterIndex, Reflects.getFQNClassName(method.getDeclaringClass()) + "#" + method.getName()));
        }
        Parameter parameter = method.getParameters()[parameterIndex];
        Class parameterClass = parameter.getType();

        String shortName = null;
        String longOptionName = null;

        boolean required;
        boolean isFlag = Primitives.isBoolean(parameterClass);
        boolean hasArgN;
        boolean hasArg1;
        @Nullable
        String argName = null;
        boolean argOptional = false;
        Class converterClass = DefaultConverter.class;
        String defaultValueString = "";
        char valueSeparator = ',';
        @NonNull
        String desc = "";

        AnnotationParameterValueList parameterValueList = annotationInfo.getParameterValues(true);
        if (parameterValueList != null) {
            shortName = (String) parameterValueList.getValue("shortName");
            longOptionName = (String) parameterValueList.getValue("value");
            argName = Strings.trimToNull((String) parameterValueList.getValue("argName"));
            if (isFlag) {
                isFlag = (boolean) parameterValueList.getValue("isFlag");
            }
            converterClass = ((AnnotationClassRef) parameterValueList.getValue("converter")).loadClass();
            defaultValueString = ((String) parameterValueList.getValue("defaultValue")).trim();
            valueSeparator = (char) parameterValueList.getValue("valueSeparator");
            desc = (String) parameterValueList.getValue("desc");
        }
        if(Objs.isEmpty(longOptionName)){
            throw new MalformedCommandException(StringTemplates.formatWithPlaceholder("Illegal option value for option {} in command {}, value (the long name) should be not empty", longOptionName, commandKey));
        }
        boolean optionShortNameSingleCharEnabled = Booleans.truth(env.getProperty(OPTION_SHORT_NAME_SINGLE_CHAR_PROP, "true"));
        if(optionShortNameSingleCharEnabled) {
            if (Objs.length(shortName) > 1) {
                throw new MalformedCommandException(StringTemplates.formatWithPlaceholder("Illegal option shortName for option {} in command {}, short name should be only one letter", shortName, commandKey));
            }
        }

        shortName = Strings.trimToNull(shortName);

        String defaultValue = null;
        String[] defaultValues = null;
        Class elementType = Commands.getConverterTargetClass(parameter, true);
        Converter converter = Commands.newConverter(converterClass, elementType);
        if (isFlag) {
            hasArg1 = false;
            hasArgN = false;
            required = false;
            argName = null;
            defaultValue = "false";
        } else {
            hasArgN = parameterClass.isArray() || Reflects.isSubClassOrEquals(Collection.class, parameterClass);
            hasArg1 = !hasArgN;
            if (argName == null) {
                argName = longOptionName;
            }
            // 默认值为 null
            if (Commands.isNullDefaultValue(defaultValueString)) {
                required = Reflects.hasAnnotation(parameter, NonNull.class);
                argOptional = !required;
            } else {
                required = false;
                argOptional = true;

                if (hasArgN) {
                    String[] values = Strings.split(defaultValueString, valueSeparator + "");
                    // 这个过程如果没有异常，那么可以直接将 values作为 defaultValues使用
                    Commands.checkOptionOrArgumentDefaultValues(values, converter, longOptionName, commandKey);
                    defaultValues = values;
                } else {
                    // 这个过程如果没有异常，那么可以直接将 defaultValueString 作为 defaultValue使用
                    Commands.checkOptionOrArgumentDefaultValue(defaultValueString, converter, longOptionName, commandKey);
                    defaultValue = defaultValueString;
                }
            }
        }

        if (Strings.isBlank(desc)) {
            desc = longOptionName;
        }

        try {
            CommandOption option = new CommandOption(shortName, longOptionName, hasArg1, desc);
            option.setOptionalArg(argOptional);
            option.setArgName(argName);
            option.setRequired(required);
            if (hasArgN) {
                option.setArgs(Option.UNLIMITED_VALUES);
            }
            option.setType(elementType);
            option.setConverter(converter);
            if (!isFlag) {
                if (hasArgN) {
                    option.setDefaultValues(defaultValues);
                    option.setValueSeparator(valueSeparator);
                } else {
                    option.setDefaultValue(defaultValue);
                }
            }
            // flag 不需要设置default value

            return option;
        } catch (Throwable e) {
            throw new MalformedCommandException(e);
        }
    }

}
