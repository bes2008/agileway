package com.jn.agileway.text.pinyin.tests.pinyin4j;

import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.collection.PrimitiveArrays;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Function;
import net.sourceforge.pinyin4j.PinyinHelper;
import org.junit.Test;

public class Pinyin4jTests {

    private static char[] chars = {'春', '江', '花', '月', '夜', '别', '说', '话'};


    @Test
    public void toHanyuPinyinStringArrayTest() {
        System.out.println("==========toHanyuPinyinStringArrayTest  start======");
        testChar(chars, new Function<Character, String[]>() {
            @Override
            public String[] apply(Character input) {
                return PinyinHelper.toHanyuPinyinStringArray(input);
            }
        });
        System.out.println("==========toHanyuPinyinStringArrayTest  end ======");
    }

    @Test
    public void toTongyongPinyinStringArrayTest(){
        System.out.println("==========toTongyongPinyinStringArrayTest  start======");
        testChar(chars, new Function<Character, String[]>() {
            @Override
            public String[] apply(Character input) {
                return PinyinHelper.toTongyongPinyinStringArray(input);
            }
        });
        System.out.println("==========toTongyongPinyinStringArrayTest  end ======");
    }


    @Test
    public void toWadeGilesPinyinStringArrayTest(){
        System.out.println("==========toWadeGilesPinyinStringArrayTest  start======");
        testChar(chars, new Function<Character, String[]>() {
            @Override
            public String[] apply(Character input) {
                return PinyinHelper.toWadeGilesPinyinStringArray(input);
            }
        });
        System.out.println("==========toWadeGilesPinyinStringArrayTest  end ======");
    }

    private void testChar(char[] chars, Function<Character, String[]> converter) {
        Pipeline.of(PrimitiveArrays.wrap(chars, false))
                .map(converter)
                .forEach(new Consumer<String[]>() {
                    @Override
                    public void accept(String[] strings) {
                        printStrings(strings);
                    }
                });
    }

    private static void printStrings(String[] strings) {
        System.out.println(Strings.join(",", strings));
    }
}
