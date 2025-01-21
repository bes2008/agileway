package com.jn.agileway.shell.command;

import com.jn.agileway.shell.exception.MalformedCommandException;
import com.jn.agileway.shell.result.CmdOutputTransformer;
import com.jn.agileway.shell.result.RawTextOutputTransformer;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.environment.Environment;
import com.jn.langx.util.Booleans;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
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
    private static final String BUILTIN_PACKAGE="com.jn.agileway.shell.builtin";

    public CommandsScanConfig buildScanConfig(Environment env) {
        boolean defaultScannerEnabled = Booleans.truth( env.getProperty(SCAN_ENABLED_PROP, "true"));
        String scanPackages = env.getProperty(SCAN_PACKAGES_PROP);
        boolean scanBuiltinPackage = Booleans.truth(env.getProperty(SCAN_BUILTIN_PACKAGES, "true"));
        CommandsScanConfig commandsScanConfig = new CommandsScanConfig();
        commandsScanConfig.setEnabled(defaultScannerEnabled);
        commandsScanConfig.setBuiltinPackagesEnabled(scanBuiltinPackage);
        List<String> scanPackageList = Pipeline.of(Strings.split(scanPackages,",")).addIf(BUILTIN_PACKAGE, new Predicate2<Collection<String>, String>() {
            @Override
            public boolean test(Collection<String> packages, String string) {
                return scanBuiltinPackage;
            }
        }).distinct().asList();

        if(defaultScannerEnabled && Objs.isEmpty(scanPackageList)){
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

        if(!scanConfig.isEnabled() || Objs.isEmpty(scanConfig.getPackages())){
            return result;
        }

        ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .enableMethodInfo()
                .enableAnnotationInfo()
                .addClassLoader(this.getClass().getClassLoader())
                .acceptPackages(Collects.toArray(scanConfig.getPackages(), String[].class)).scan();
        ClassInfoList commandClassInfoList = scanResult.getClassesWithAnnotation(com.jn.agileway.shell.command.annotation.CommandComponent.class);
        commandClassInfoList = commandClassInfoList.filter(new ClassInfoList.ClassInfoFilter() {
            @Override
            public boolean accept(ClassInfo classInfo) {
                if(classInfo.isAnnotation() || classInfo.isInterface()
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

    private Pair<CommandGroup, List<Command>> resolveCommandClass(ClassInfo classInfo){
        classInfo.getPackageInfo();
        CommandGroup commandGroup = createCommandGroup(classInfo);
        MethodInfoList methodInfoList = classInfo.getDeclaredMethodInfo();
        List<Command> commands = Lists.newArrayList();

        Pair<CommandGroup, List<Command>> result = new Pair<>(commandGroup, commands);
        for (int i = 0; i < methodInfoList.size(); i++) {
            MethodInfo methodInfo = methodInfoList.get(i);
            if(!methodInfo.isPublic() || methodInfo.isAbstract() || methodInfo.isConstructor() || methodInfo.isNative() || methodInfo.isStatic()){
                continue;
            }
            if(!methodInfo.hasAnnotation(com.jn.agileway.shell.command.annotation.Command.class)){
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

        if(outputTransformerClass==null && packageAnnotationed){
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

        if (outputTransformerClass != null){
            CmdOutputTransformer transformer = Reflects.<CmdOutputTransformer>newInstance(outputTransformerClass);
            group.setOutputTransformer(transformer);
        }

        return group;
    }

    private Command createCommand(MethodInfo methodInfo){
        AnnotationInfo annotationInfo = methodInfo.getAnnotationInfo(com.jn.agileway.shell.command.annotation.Command.class);
        AnnotationParameterValueList parameterValueList = annotationInfo.getParameterValues(true);

        String name = null;
        String[] alias=null;
        String desc =null;
        Class outputTransformerClass = null;
        if (parameterValueList!=null){
            name = (String)parameterValueList.getValue("value");
            alias = (String[]) parameterValueList.getValue("alias");
            desc = (String)parameterValueList.getValue("desc");
            AnnotationClassRef outputTransformerClassRef = ((AnnotationClassRef) parameterValueList.getValue("outputTransformer"));
            if (outputTransformerClassRef != null) {
                outputTransformerClass = outputTransformerClassRef.loadClass();
            }
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
        CmdOutputTransformer transformer=null;
        if (outputTransformerClass != null){
            transformer = Reflects.<CmdOutputTransformer>newInstance(outputTransformerClass);
        }
        command.setOutputTransformer(transformer);

        MethodParameterInfo[] methodParameterInfoList = methodInfo.getParameterInfo();
        List<Option> options = Lists.newArrayListWithCapacity(methodParameterInfoList.length);
        for (int i = 0; i < methodParameterInfoList.length ; i++) {
            MethodParameterInfo methodParameterInfo = methodParameterInfoList[i];
            Option option = createOption(methodParameterInfo, method, i);
            options.add(option);
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
        Class elementType;
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
            optionName = (String)parameterValueList.getValue("value");
            longOptionName = (String) parameterValueList.getValue("longName");
            required = (boolean)parameterValueList.getValue("required");
            hasArg1 = (boolean)parameterValueList.getValue("hasArg");
            hasArgN=(boolean)parameterValueList.getValue("hasArgs");
            argName=(String)parameterValueList.getValue("argName");
            argOptional=(boolean)parameterValueList.getValue("argOptional");
            elementType=((AnnotationClassRef)parameterValueList.getValue("type")).loadClass();
            converterClass = ((AnnotationClassRef)parameterValueList.getValue("converter")).loadClass();
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

        final Converter converter = (converterClass==null || converterClass==DefaultConverter.class )? new DefaultConverter(elementType) : Reflects.<Converter>newInstance(converterClass);
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

        if(Strings.isBlank(desc)){
            desc = Objs.useValueIfEmpty(longOptionName, optionName);
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
