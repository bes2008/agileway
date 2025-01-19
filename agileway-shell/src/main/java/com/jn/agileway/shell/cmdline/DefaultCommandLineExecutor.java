package com.jn.agileway.shell.cmdline;

import com.jn.agileway.shell.exception.MalformedOptionValueException;
import com.jn.agileway.shell.exception.UnsupportedCollectionException;
import com.jn.agileway.shell.result.CmdExecResult;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.collection.Arrs;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Sets;
import com.jn.langx.util.collection.stack.SimpleStack;
import com.jn.langx.util.enums.Enums;
import com.jn.langx.util.reflect.Reflects;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.lang.reflect.Array;
import java.lang.reflect.Parameter;
import java.util.*;
import java.lang.reflect.Method;
import java.util.concurrent.LinkedBlockingDeque;

public class DefaultCommandLineExecutor implements CommandLineExecutor {
    private CmdExecContext cmdExecContext;

    public DefaultCommandLineExecutor() {
    }

    public void setCmdExecContext(CmdExecContext cmdExecContext) {
        this.cmdExecContext = cmdExecContext;
    }

    @Override
    public CmdExecContext getCmdExecContext() {
        return cmdExecContext;
    }

    public CmdExecResult exec(Cmdline cmdline) {
        CmdExecResult cmdExecResult = new CmdExecResult();
        Method method = cmdline.getCommandDefinition().getMethod();
        Object[] methodArgs = prepareMethodArgs(cmdline);
        Object component = getCmdExecContext().getComponentFactory().get(method.getDeclaringClass());
        Object methodResult = Reflects.invokeMethod(method, component, methodArgs);
        cmdExecResult.setStdoutData(methodResult);
        return cmdExecResult;
    }

    private Object[] prepareMethodArgs(Cmdline cmdline) {
        List<String> optionKeys = cmdline.getCommandDefinition().getOptionKeys();
        Object[] methodArgs = new Object[optionKeys.size()];
        Method method = cmdline.getCommandDefinition().getMethod();
        Parameter[] parameters = method.getParameters();
        Options optionsDef = cmdline.getCommandDefinition().getOptions();
        for (int parameterIndex = 0; parameterIndex < optionKeys.size(); parameterIndex++) {
            String optionKey = optionKeys.get(parameterIndex);
            Parameter parameter = parameters[parameterIndex];
            Option optionDef = optionsDef.getOption(optionKey);
            if (parameter.getType().isArray()) {
                String[] stringValues = cmdline.getParsed().getOptionValues(optionKey);
                Object array = Arrs.createArray((Class) optionDef.getType(), stringValues.length);
                for (int i = 0; i < stringValues.length; i++) {
                    String stringValue = stringValues[i];
                    Object value = convertStringToTarget(stringValue, (Class) optionDef.getType(), optionDef);
                    Array.set(array, i, value);
                }
                methodArgs[parameterIndex] = array;
            } else if (Reflects.isSubClassOrEquals(Collection.class, parameter.getType())) {
                String[] stringValues = cmdline.getParsed().getOptionValues(optionKey);
                Collection collection = newCollection(parameter.getType());
                for (String stringValue : stringValues) {
                    Object value = convertStringToTarget(stringValue, (Class) optionDef.getType(), optionDef);
                    collection.add(value);
                }
                methodArgs[parameterIndex] = collection;
            } else {
                String stringValue = cmdline.getParsed().getOptionValue(optionKey);
                Object value = convertStringToTarget(stringValue, (Class) optionDef.getType(), optionDef);
                methodArgs[parameterIndex] = value;
            }
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
}
