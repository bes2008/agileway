package com.jn.agileway.shell.cmdline;

import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;

import java.util.List;

public class ShellCmdlines {
    public static String[] cmdlineToArgs(String cmdline){
        if(cmdline==null){
            return null;
        }
        if(Strings.isBlank(cmdline)){
            return Emptys.EMPTY_STRINGS;
        }
        List<String> args = new ShellLineTokenizer(cmdline).tokenize();
        String[] result = new String[args.size()];
        for (int i=0;i<args.size();i++){
            String arg = args.get(i);
            if(arg.length()>1) {
                if ((Strings.startsWith(arg, "'") && Strings.endsWith(arg, "'")) || (Strings.startsWith(arg, "\"") && Strings.endsWith(arg, "\""))) {
                    result[i] = Strings.substring(arg, 1, args.size() - 1);
                }
            }
            result[i]=arg;
        }
        return result;
    }


    public static String cmdlineToString(String[] cmdline){
        if(cmdline==null){
            return null;
        }
        if(cmdline.length==0){
            return "";
        }
        StringBuilder builder = new StringBuilder(100);
        for (int i=0;i<cmdline.length;i++){
            if(i>0){
                builder.append(" ");
            }
            String arg = cmdline[i];
            if(Strings.containsAny(arg, Strings.TAB, Strings.SP)){
                builder.append('"').append(arg).append('"');
            }
        }
        return builder.toString();
    }
}
