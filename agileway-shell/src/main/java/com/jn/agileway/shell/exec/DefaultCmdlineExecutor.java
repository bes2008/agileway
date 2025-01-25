package com.jn.agileway.shell.exec;

import com.jn.agileway.shell.command.Command;
import com.jn.agileway.shell.command.CommandArgument;
import com.jn.agileway.shell.command.CommandOption;
import com.jn.agileway.shell.exception.MalformedCommandArgumentsException;
import com.jn.agileway.shell.exception.MalformedOptionValueException;
import com.jn.agileway.shell.exception.UnsupportedCollectionException;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.collection.Arrs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Sets;
import com.jn.langx.util.collection.stack.SimpleStack;
import com.jn.langx.util.enums.Enums;
import com.jn.langx.util.reflect.Reflects;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.lang.reflect.Array;
import java.lang.reflect.Parameter;
import java.util.*;
import java.lang.reflect.Method;
import java.util.concurrent.LinkedBlockingDeque;

public final class DefaultCmdlineExecutor extends CmdlineExecutor<CommandLine> {

    public DefaultCmdlineExecutor() {
        this(true);
    }

    public DefaultCmdlineExecutor(boolean stopParseAtNonDefinedOption) {
        this.setCmdlineParser(new DefaultCmdlineParser(stopParseAtNonDefinedOption));
    }

    @Override
    public boolean isExecutable(Command command) {
        return true;
    }

    protected Object internalExecute(Cmdline<CommandLine> cmdline) {
        Method method = cmdline.getCommand().getMethod();
        Object[] methodArgs = prepareMethodArgs(cmdline);
        Object component = getComponentFactory().get(method.getDeclaringClass());
        Object methodResult = Reflects.invokeMethod(method, component, methodArgs);
        return methodResult;
    }

    private Object[] prepareMethodArgs(Cmdline<CommandLine> cmdline) {
        List<String> optionKeys = cmdline.getCommand().getOptionKeys();
        Object[] methodArgs = new Object[cmdline.getCommand().getMethod().getParameters().length];
        Method method = cmdline.getCommand().getMethod();
        Parameter[] parameters = method.getParameters();

        int parameterIndex = 0;
        // 处理 options
        Options optionsDef = cmdline.getCommand().getOptions();
        for (; parameterIndex < optionKeys.size(); parameterIndex++) {
            String optionKey = optionKeys.get(parameterIndex);
            Parameter parameter = parameters[parameterIndex];
            Option optionDef = optionsDef.getOption(optionKey);

            if (parameter.getType().isArray()) {
                String[] stringValues = getOptionValues(cmdline, optionKey);

                Object array = Arrs.createArray((Class) optionDef.getType(), stringValues == null ? 0 : stringValues.length);
                if (stringValues != null) {
                    for (int i = 0; i < stringValues.length; i++) {
                        String stringValue = stringValues[i];
                        Object value = convertStringToTarget(stringValue, (Class) optionDef.getType(), optionDef);
                        Array.set(array, i, value);
                    }
                }
                methodArgs[parameterIndex] = array;
            } else if (Reflects.isSubClassOrEquals(Collection.class, parameter.getType())) {
                String[] stringValues = getOptionValues(cmdline, optionKey);
                Collection collection = newCollection(parameter.getType());
                if (stringValues != null) {
                    for (String stringValue : stringValues) {
                        Object value = convertStringToTarget(stringValue, (Class) optionDef.getType(), optionDef);
                        collection.add(value);
                    }
                }
                methodArgs[parameterIndex] = collection;
            } else {
                String stringValue = getOptionValue(cmdline, optionKey);
                Object value = convertStringToTarget(stringValue, (Class) optionDef.getType(), optionDef);
                methodArgs[parameterIndex] = value;
            }
        }

        List<CommandArgument> argumentsDef = cmdline.getCommand().getArguments();
        if (!argumentsDef.isEmpty()) {
            List<String> argumentValues = cmdline.getParsed().getArgList();
            if(argumentValues.size() < argumentsDef.size()-1){
                throw new MalformedCommandArgumentsException("Insufficient command arguments");
            }
            if(argumentValues.size() == argumentsDef.size()-1  && argumentsDef.get(argumentsDef.size()-1).isRequired()){
                throw new MalformedCommandArgumentsException("Insufficient command arguments");
            }
            if(argumentValues.size()> argumentsDef.size() && !argumentsDef.get(argumentsDef.size()-1).isMultipleValue()){
                throw new MalformedCommandArgumentsException("Too many command arguments");
            }
            int i = 0;
            for (; i < argumentsDef.size() - 1; i++) {
                CommandArgument commandArgument = argumentsDef.get(i);
                methodArgs[parameterIndex] = convertStringToTarget(argumentValues.get(i), commandArgument.getType(), commandArgument);
                parameterIndex++;
            }

            // 最后一个参数
            List<String> lastArgumentValues = argumentValues.subList(i, argumentValues.size());
            Parameter lastParameter = parameters[parameterIndex];
            CommandArgument lastCommandArgument = argumentsDef.get(i);
            if (lastParameter.getType().isArray()) {
                String[] stringValues = lastArgumentValues.isEmpty()?lastCommandArgument.getDefaultValues(): Collects.toArray(lastArgumentValues, String[].class);
                if(stringValues==null){
                    methodArgs[parameterIndex] = null;
                }else {
                    Class elementType = lastCommandArgument.getType();
                    Object array = Arrs.createArray(elementType, stringValues.length);
                    for (int j = 0; j < stringValues.length; j++) {
                        String stringValue = stringValues[j];
                        Object value = convertStringToTarget(stringValue, elementType, lastCommandArgument);
                        Array.set(array, j, value);
                    }
                    methodArgs[parameterIndex] = array;
                }
            } else {
                String value = lastArgumentValues.isEmpty() ? lastCommandArgument.getDefaultValue():lastArgumentValues.get(0);
                methodArgs[parameterIndex] = convertStringToTarget(value, lastCommandArgument.getType(), lastCommandArgument);
            }
            parameterIndex++;
        }
        if (parameterIndex != parameters.length) {
            throw new MalformedCommandArgumentsException("argument missing or too many");
        }
        return methodArgs;
    }


    private Collection newCollection(Class collectionClass) {
        if (collectionClass.isInterface()) {
            if (collectionClass == Collection.class || collectionClass == List.class) {
                return Lists.newArrayList();
            } else if (collectionClass == Stack.class) {
                return new SimpleStack();
            } else if (collectionClass == Set.class) {
                return Sets.newHashSet();
            } else if (collectionClass == SortedSet.class) {
                return Sets.newTreeSet();
            } else if (Reflects.isSubClassOrEquals(Queue.class, collectionClass)) {
                return new LinkedBlockingDeque();
            }
            throw new UnsupportedCollectionException(StringTemplates.formatWithPlaceholder("unsupported collection class: {}", Reflects.getFQNClassName(collectionClass)));
        } else {
            return (Collection) Reflects.newInstance(collectionClass);
        }
    }

    private String[] getOptionValues(Cmdline<CommandLine> cmdline, String optionKey) {
        Option optionDef = cmdline.getCommand().getOptions().getOption(optionKey);
        if (!cmdline.getParsed().hasOption(optionKey)) {
            CommandOption commandOption = (CommandOption) optionDef;
            return commandOption.getDefaultValues();
        } else {
            return cmdline.getParsed().getOptionValues(optionKey);
        }
    }

    private String getOptionValue(Cmdline<CommandLine> cmdline, String optionKey) {
        Option optionDef = cmdline.getCommand().getOptions().getOption(optionKey);
        if (optionDef.getArgs() < 0) {
            // is flag
            return cmdline.getParsed().hasOption(optionKey) ? "true" : "false";
        }
        if (!cmdline.getParsed().hasOption(optionKey)) {
            CommandOption commandOption = (CommandOption) optionDef;
            return commandOption.getDefaultValue();
        } else {
            return cmdline.getParsed().getOptionValue(optionKey);
        }
    }


    private Object convertStringToTarget(String stringValue, Class type, Option optionDef) {
        if (type.isEnum()) {
            return Enums.inferEnum(type, stringValue);
        } else if (type == String.class) {
            return stringValue;
        } else {
            try {
                Object value = optionDef.getConverter().apply(stringValue);
                return value;
            } catch (Throwable ex) {
                throw new MalformedOptionValueException(ex);
            }
        }
    }

    private Object convertStringToTarget(String stringValue, Class type, CommandArgument commandArgument) {
        if (type.isEnum()) {
            return Enums.inferEnum(type, stringValue);
        } else if (type == String.class) {
            return stringValue;
        } else {
            try {
                Object value = commandArgument.getConverter().apply(stringValue);
                return value;
            } catch (Throwable ex) {
                throw new MalformedOptionValueException(ex);
            }
        }
    }

}
