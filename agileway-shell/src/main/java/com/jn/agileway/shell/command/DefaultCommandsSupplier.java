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
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.reflect.type.Primitives;
import com.jn.langx.util.struct.Holder;
import com.jn.langx.util.struct.Pair;
import io.github.classgraph.*;
import org.apache.commons.cli.Converter;
import org.apache.commons.cli.Option;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultCommandsSupplier implements CommandsSupplier {
    private static final String SCAN_ENABLED_PROP = "agileway.shell.scan.default.enabled";
    private static final String SCAN_PACKAGES_PROP = "agileway.shell.scan.default.packages";

    private static final String SCAN_BUILTIN_PACKAGES = "agileway.shell.scan.default.builtin.enabled";
    private static final String BUILTIN_PACKAGE = "com.jn.agileway.shell.builtin";

    public CommandsScanConfig buildScanConfig(Environment env) {
        boolean defaultScannerEnabled = Booleans.truth(env.getProperty(SCAN_ENABLED_PROP, "true"));
        String scanPackages = env.getProperty(SCAN_PACKAGES_PROP);
        boolean scanBuiltinPackage = Booleans.truth(env.getProperty(SCAN_BUILTIN_PACKAGES, "true"));
        CommandsScanConfig commandsScanConfig = new CommandsScanConfig();
        commandsScanConfig.setEnabled(defaultScannerEnabled);
        commandsScanConfig.setBuiltinPackagesEnabled(scanBuiltinPackage);
        List<String> scanPackageList = Pipeline.of(Strings.split(scanPackages, ",")).addIf(BUILTIN_PACKAGE, new Predicate2<Collection<String>, String>() {
            @Override
            public boolean test(Collection<String> packages, String string) {
                return scanBuiltinPackage;
            }
        }).distinct().asList();

        if (defaultScannerEnabled && Objs.isEmpty(scanPackageList)) {
            Loggers.getLogger(DefaultCommandsSupplier.class).warn("property is empty: {}", SCAN_PACKAGES_PROP);
        }
        commandsScanConfig.setPackages(scanPackageList);
        return commandsScanConfig;
    }

    @Override
    public Map<CommandGroup, List<Command>> get(Environment env) {
        return scan(buildScanConfig(env));
    }

    private Map<CommandGroup, List<Command>> scan(CommandsScanConfig scanConfig) {
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
            Pair<CommandGroup, List<Command>> groupCommandsEntry = resolveCommandClass(classInfo);
            result.put(groupCommandsEntry.getKey(), groupCommandsEntry.getValue());
        }

        return result;
    }

    private Pair<CommandGroup, List<Command>> resolveCommandClass(ClassInfo classInfo) {
        classInfo.getPackageInfo();
        CommandGroup commandGroup = createCommandGroup(classInfo);
        MethodInfoList methodInfoList = classInfo.getDeclaredMethodInfo();
        List<Command> commands = Lists.newArrayList();

        Pair<CommandGroup, List<Command>> result = new Pair<>(commandGroup, commands);
        for (int i = 0; i < methodInfoList.size(); i++) {
            MethodInfo methodInfo = methodInfoList.get(i);
            if (!methodInfo.isPublic() || methodInfo.isAbstract() || methodInfo.isConstructor() || methodInfo.isNative() || methodInfo.isStatic()) {
                continue;
            }
            if (!methodInfo.hasAnnotation(com.jn.agileway.shell.command.annotation.Command.class)) {
                continue;
            }
            Command command = createCommand(methodInfo);
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

    private Command createCommand(MethodInfo methodInfo) {
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
                Option option = createOption(command.getName(), methodParameterInfo, method, i);
                options.add(option);
            } else {
                CommandArgument commandArgument = createCommandArgument(methodParameterInfo, method, i);
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

        return command;
    }

    private CommandArgument createCommandArgument(MethodParameterInfo methodParameterInfo, Method method, int parameterIndex) {
        AnnotationInfo annotationInfo = methodParameterInfo.getAnnotationInfo(com.jn.agileway.shell.command.annotation.CommandArgument.class);
        if (annotationInfo == null) {
            throw new MalformedCommandException(StringTemplates.formatWithPlaceholder("@CommandArgument at the {}th parameter is required for method {}", parameterIndex, Reflects.getFQNClassName(method.getDeclaringClass()) + "#" + method.getName()));
        }

        Class parameterType = method.getParameters()[parameterIndex].getType();
        if (parameterIndex < method.getParameters().length - 1) {
            if (parameterType != String.class) {
                throw new IllegalArgumentException(StringTemplates.formatWithPlaceholder("parameter type should be java.lang.String at {}th for method {}", parameterIndex, Reflects.getFQNClassName(method.getDeclaringClass()) + "#" + method.getName()));
            }
        } else if (parameterIndex == method.getParameters().length - 1) {
            if (parameterType == String.class || (parameterType.isArray() && parameterType.getComponentType() == String.class)) {
                // matching
            } else {
                throw new IllegalArgumentException(StringTemplates.formatWithPlaceholder("parameter type should be java.lang.String or java.lang.String[] at {}th for method {}", parameterIndex, Reflects.getFQNClassName(method.getDeclaringClass()) + "#" + method.getName()));
            }
        }


        AnnotationParameterValueList parameterValueList = annotationInfo.getParameterValues(true);
        String name = (String) parameterValueList.getValue("value");
        String desc = (String) parameterValueList.getValue("desc");
        boolean required = (boolean) parameterValueList.getValue("required");

        if (Strings.isBlank(name)) {
            throw new MalformedCommandException(StringTemplates.formatWithPlaceholder("@CommandArgument value() at the {}th parameter is required for method {}", parameterIndex, Reflects.getFQNClassName(method.getDeclaringClass()) + "#" + method.getName()));
        }
        desc = Objs.useValueIfEmpty(desc, name);

        CommandArgument argument = new CommandArgument();
        argument.setRequired(required);
        argument.setName(name);
        argument.setDesc(desc);
        return argument;
    }

    private CommandOption createOption(final String commandKey, MethodParameterInfo methodParameterInfo, Method method, int parameterIndex) {
        AnnotationInfo annotationInfo = methodParameterInfo.getAnnotationInfo(com.jn.agileway.shell.command.annotation.CommandOption.class);

        Parameter parameter = method.getParameters()[parameterIndex];
        Class parameterClass = parameter.getType();

        @Nullable final Holder<String> optionName = new Holder<String>();
        String longOptionName = null;

        boolean required;
        boolean isFlag = Primitives.isBoolean(parameterClass);
        boolean hasArgN;
        boolean hasArg1;
        @Nullable
        String argName = null;
        boolean argOptional = false;
        Class elementType = null;
        Class converterClass = DefaultConverter.class;
        String defaultValueString = "";
        char valueSeparator = ',';
        @NonNull
        String desc = "";

        AnnotationParameterValueList parameterValueList = null;
        if (annotationInfo != null) {
            parameterValueList = annotationInfo.getParameterValues(true);
        }
        if (parameterValueList != null) {
            optionName.set((String) parameterValueList.getValue("value"));
            longOptionName = (String) parameterValueList.getValue("longName");
            argName = Strings.trimToNull((String) parameterValueList.getValue("argName"));
            if (isFlag) {
                isFlag = (boolean) parameterValueList.getValue("isFlag");
            }
            converterClass = ((AnnotationClassRef) parameterValueList.getValue("converter")).loadClass();
            defaultValueString = (String) parameterValueList.getValue("defaultValue");
            valueSeparator = (char) parameterValueList.getValue("valueSeparator");
            desc = (String) parameterValueList.getValue("desc");

        } else {
            optionName.set(methodParameterInfo.getName());
        }

        if (!optionName.isEmpty() && Objs.length(optionName.get()) > 1) {
            throw new MalformedCommandException(StringTemplates.formatWithPlaceholder("Illegal option shortName for option {} in command {}, short name should be only one letter", optionName.get(), commandKey));
        }


        String defaultValue = null;
        String[] defaultValues = null;
        Holder<Converter> converterHolder = new Holder<>();
        if (isFlag) {
            hasArg1 = false;
            hasArgN = false;
            elementType = parameterClass;
            required = false;
            argName = null;
            converterHolder.set(new DefaultConverter(boolean.class));
            defaultValue = "false";
        } else {
            hasArgN = parameterClass.isArray() || Reflects.isSubClassOrEquals(Collection.class, parameterClass);
            hasArg1 = !hasArgN;
            if (argName == null) {
                argName = longOptionName;
            }
            if (hasArgN) {
                if (parameterClass.isArray()) {
                    elementType = parameterClass.getComponentType();
                } else {
                    ParameterizedType parameterizedType = (ParameterizedType) parameter.getParameterizedType();
                    elementType = (Class) parameterizedType.getActualTypeArguments()[0];
                }
            } else {
                elementType = parameterClass;
            }

            Converter converter = (converterClass == null || converterClass == DefaultConverter.class) ? new DefaultConverter(elementType) : Reflects.<Converter>newInstance(converterClass);
            converterHolder.set(converter);
            // 默认值为 null
            if (Objs.equals(com.jn.agileway.shell.command.annotation.CommandOption.NULL, defaultValueString)) {
                required = Reflects.hasAnnotation(parameter, NonNull.class);
                argOptional = !required;
            } else {
                required = false;
                argOptional = true;

                if (hasArgN) {
                    String[] values = Strings.split(defaultValueString, valueSeparator + "");
                    // 这个过程如果没有异常，那么可以直接将 values作为 defaultValues使用
                    Pipeline.of(values).map(new Function<String, Object>() {
                        @Override
                        public Object apply(String value) {
                            try {
                                return converterHolder.get().apply(value);
                            } catch (Throwable e) {
                                throw new MalformedCommandException(StringTemplates.formatWithPlaceholder("Illegal defaultValues for option {} in command {}", optionName.get(), commandKey));
                            }
                        }
                    }).asList();
                    defaultValues = values;
                } else {
                    // 这个过程如果没有异常，那么可以直接将 defaultValueString 作为 defaultValue使用
                    try {
                        converterHolder.get().apply(defaultValueString);
                    } catch (Throwable e) {
                        throw new MalformedCommandException(StringTemplates.formatWithPlaceholder("Illegal defaultValue for option {} in command {}", optionName.get(), commandKey));
                    }
                    defaultValue = defaultValueString;
                }
            }
        }

        if (Strings.isBlank(desc)) {
            desc = Objs.useValueIfEmpty(longOptionName, optionName.get());
        }

        try {
            CommandOption option = new CommandOption(optionName.get(), longOptionName, hasArg1, desc);
            option.setOptionalArg(argOptional);
            option.setArgName(argName);
            option.setRequired(required);
            if (hasArgN) {
                option.setArgs(Option.UNLIMITED_VALUES);
            }
            option.setType(elementType);
            option.setConverter(converterHolder.get());
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
