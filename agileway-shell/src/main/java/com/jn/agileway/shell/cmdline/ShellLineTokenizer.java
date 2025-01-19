package com.jn.agileway.shell.cmdline;

import com.jn.langx.text.tokenizer.CommonTokenizer;
import com.jn.langx.text.tokenizer.TokenFactory;
import com.jn.langx.util.Strings;

public class ShellLineTokenizer extends CommonTokenizer<String> implements CmdlineTokenizer{

    public ShellLineTokenizer(String cmdline) {
        super(cmdline, false);

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

    @Override
    protected String getIfDelimiterStart(long position, char ch) {
        if(ch==' '){
            if(position==0){
                return ch+"";
            }
            char previousChar = this.getBuffer().get(position-1);
            if(previousChar=='\\'){
                return null;
            }
        }else if(ch == '\t'){
            return ch+"";
        }
        return null;
    }
}
