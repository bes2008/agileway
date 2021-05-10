package com.jn.agileway.text.pinyin.impl.langx;

import com.jn.agileway.text.pinyin.AbstractPinyinEngine;
import com.jn.langx.text.pinyin.Pinyin;

public class LangxPinyinEngine extends AbstractPinyinEngine {
    @Override
    public String getName() {
        return "langx_pinyin";
    }

    @Override
    public String getPinyin(char c) {
        return Pinyin.toPinyin(c);
    }

    @Override
    public String getPinyin(String str, String separator) {
        return Pinyin.toPinyin(str, separator);
    }

    @Override
    public String getFirstLetter(String str, String separator) {
        return Pinyin.toPinyin(str, separator);
    }
}
