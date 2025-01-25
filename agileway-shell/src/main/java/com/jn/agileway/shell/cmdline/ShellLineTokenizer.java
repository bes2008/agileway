package com.jn.agileway.shell.cmdline;

import com.jn.agileway.shell.exec.CmdlineTokenizer;
import com.jn.langx.text.tokenizer.CommonTokenizer;
import com.jn.langx.text.tokenizer.TokenFactory;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Lists;

import java.util.List;

public class ShellLineTokenizer extends CommonTokenizer<String> implements CmdlineTokenizer {
    private List<Character> quoteStack = Lists.newArrayListWithCapacity(10);
    public ShellLineTokenizer(String cmdline) {
        super(removeSemicolon(cmdline), false);

        this.tokenFactory = new TokenFactory<String>() {
            @Override
            public String get(String tokenContent, Boolean isDelimiter) {
                if(tokenContent.length()>2 && Strings.startsWith(tokenContent,"\"") && Strings.endsWith(tokenContent, "\"")){
                    return Strings.substring(tokenContent,1,tokenContent.length()-1);
                }
                return tokenContent;
            }
        };
    }

    /**
     * 移除分号
     */
    private static String removeSemicolon(String cmdline){
        if(cmdline==null || Strings.isBlank( cmdline)){
            return "";
        }
        cmdline = Strings.trim(cmdline);
        if(Strings.endsWith(cmdline,";")){
            cmdline = Strings.substring(cmdline, 0, cmdline.length()-1);
        }
        return cmdline;
    }

    @Override
    protected String getIfDelimiterStart(long position, char ch) {
        String result = null;
        switch(ch){
            case '"':
            case '\'':
                if(quoteStack.isEmpty()) {
                    // 进入 " " 或者 ' '引号区域
                    quoteStack.add(ch);
                }else {
                    int index = quoteStack.lastIndexOf(ch);
                    if(index<0){
                        // 进入另一个引号
                        quoteStack.add(ch);
                    }else{
                        // 引号结束
                        for (int removedIndex = quoteStack.size()-1; removedIndex>=index; removedIndex--){
                            quoteStack.remove(removedIndex);
                        }
                    }

                }
                break;
            case '\t':
            case ' ':
                if(!quoteStack.isEmpty()){
                    // 当前字符是在 引号范围之内
                    // ignore it
                }else{
                    result = ch+"";
                }
                break;
            default:
                break;
        }
        return result;
    }
}
