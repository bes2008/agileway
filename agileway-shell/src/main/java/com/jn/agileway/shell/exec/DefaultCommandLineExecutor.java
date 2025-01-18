package com.jn.agileway.shell.exec;

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
    private CmdExecContext shellContext;

    public DefaultCommandLineExecutor() {
    }

    public void setCmdExecContext(CmdExecContext shellContext) {
        this.shellContext = shellContext;
    }

    @Override
    public CmdExecContext getCmdExecContext() {
        return shellContext;
    }

    public CmdExecResult exec(Cmdline cmdline) {
        CmdExecResult cmdExecResult = new CmdExecResult();
        Method method = cmdline.getCommandDefinition().getMethod();
        Object[] methodArgs = prepareMethodArgs(cmdline);
        Object component = getCmdExecContext().getComponentFactory().get(method.getDeclaringClass());
        Object methodResult = Reflects.invokeMethod(method, component, methodArgs);

        if(methodResult!=null){
            String cmdResult = getCmdExecContext().getMethodInvocationResultTransformer().transform(methodResult);
            cmdExecResult.setCmdResult(cmdResult);
        }
        return null;
    }

    private Object[] prepareMethodArgs(Cmdline cmdline) {
        Options options = cmdline.getCommandDefinition().getOptions();
        List<String> optionKeys = cmdline.getCommandDefinition().getOptionKeys();
        Object[] methodArgs = new Object[optionKeys.size()];
        Method method = cmdline.getCommandDefinition().getMethod();
        Parameter[] parameters = method.getParameters();
        for (int parameterIndex = 0; parameterIndex < optionKeys.size(); parameterIndex++) {
            String optionKey = optionKeys.get(parameterIndex);
            Option option = options.getOption(optionKey);
            Parameter parameter = parameters[parameterIndex];
            if (parameter.getType().isArray()) {
                List<String> stringValues = option.getValuesList();
                Object array = Arrs.createArray((Class) option.getType(), stringValues.size());
                for (int i = 0; i < stringValues.size(); i++) {
                    String stringValue = stringValues.get(i);
                    Object value = convertStringToTarget(stringValue, (Class) option.getType(), option);
                    Array.set(array, i, value);
                }
                methodArgs[parameterIndex] = array;
            } else if (Reflects.isSubClassOrEquals(Collection.class, parameter.getType())) {
                List<String> stringValues = option.getValuesList();
                Collection collection = newCollection(parameter.getType());
                for (String stringValue : stringValues) {
                    Object value = convertStringToTarget(stringValue, (Class) option.getType(), option);
                    collection.add(value);
                }
                methodArgs[parameterIndex] = collection;
            } else {
                String stringValue = option.getValue();
                Object value = convertStringToTarget(stringValue, (Class) option.getType(), option);
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

    private Object convertStringToTarget(String stringValue, Class type, Option option) {
        if (type.isEnum()) {
            return Enums.inferEnum(type, stringValue);
        } else if (type == String.class) {
            return stringValue;
        } else {
            try {
                Object value = option.getConverter().apply(stringValue);
                return value;
            } catch (Throwable ex) {
                throw new MalformedOptionValueException(ex);
            }
        }
    }
}
