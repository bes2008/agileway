package com.jn.agileway.shell.result;

import com.jn.langx.Transformer;

/**
 * CmdOutputTransformer接口继承自Transformer接口，用于定义将命令执行结果转换为字符串的逻辑
 * 它的作用是提供一个标准的方法来转换命令的输出结果，使得输出结果可以被统一处理和使用
 */
public interface CmdOutputTransformer extends Transformer<Object,String> {

    /**
     * 转换方法的结果为字符串表示形式
     * 这个方法被设计为覆盖（Override）Transformer接口的transform方法，专注于将任意类型的对象转换为字符串
     *
     * @param methodResult 一个Object类型的参数，代表命令执行的结果这个参数可以是任何类型的对象，由实现类具体指定
     * @return 返回一个String类型的对象，代表转换后的命令执行结果这个字符串可以被进一步处理或显示
     */
    @Override
    String transform(Object methodResult);
}
