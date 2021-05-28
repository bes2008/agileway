package com.jn.agileway.text.pinyin;

import java.util.List;

/**
 * 拼音引擎接口，具体的拼音实现通过实现此接口，完成具体实现功能
 */
public interface PinyinEngine {
    String getName();

    /**
     * 如果c为汉字，则返回大写拼音；如果c不是汉字，则返回String.valueOf(c)
     *
     * @param c 任意字符，汉字返回拼音，非汉字原样返回
     * @return 汉字返回拼音，非汉字原样返回
     */
    List<String> getPinyin(char c);

    /**
     * 获取字符串对应的完整拼音，非中文返回原字符
     *
     * @param str       字符串
     * @return 拼音
     */
    List<String> getPinyin(String str);


    char toSimplifiedPinyin(char c);

    String toSimplifiedPinyin(String str);

    char toTraditionalPinyin(char c);

    String toTraditionalPinyin(String str);
}