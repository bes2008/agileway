package com.jn.agileway.pinyin;

import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;

import java.util.List;

public abstract class AbstractPinyinEngine implements PinyinEngine {

    public char getFirstLetter(char c) {
        return getPinyin(c).charAt(0);
    }

    public String getFirstLetterString(String str, String separator) {
        final String splitSeparator = Strings.isEmpty(separator) ? "#" : separator;
        final String[] segments = Strings.split(getPinyin(str, splitSeparator), splitSeparator);
        List<String> stringList = Pipeline.of(segments).map(new Function<String, String>() {
            @Override
            public String apply(String s) {
                return String.valueOf(s.length() > 0 ? s.charAt(0) : "");
            }
        }).asList();
        return Strings.join(separator, stringList);
    }
}
