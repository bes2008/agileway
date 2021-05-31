package com.jn.agileway.text.pinyin;

import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;

import java.util.List;

public abstract class AbstractPinyinEngine implements PinyinEngine {

    public char getFirstLetter(char c) {
        return getPinyin(c).get(0).charAt(0);
    }

    public String getFirstLetterString(String str, String separator) {
        final String splitSeparator = Strings.isEmpty(separator) ? "#" : separator;
        final List<String> segments = getPinyin(str);
        List<String> stringList = Pipeline.of(segments).map(new Function<String, String>() {
            @Override
            public String apply(String s) {
                return String.valueOf(s.length() > 0 ? s.charAt(0) : "");
            }
        }).asList();
        return Strings.join(separator, stringList);
    }
}
