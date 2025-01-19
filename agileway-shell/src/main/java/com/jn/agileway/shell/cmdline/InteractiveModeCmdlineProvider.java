package com.jn.agileway.shell.cmdline;

import com.jn.agileway.shell.ApplicationArgs;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.io.Charsets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InteractiveModeCmdlineProvider implements CmdlineProvider{
    private ApplicationArgs appArgs;
    private boolean appArgsUsed = false;
    private BufferedReader stdin;
    private CmdlineTokenizer cmdlineTokenizer;
    public InteractiveModeCmdlineProvider(ApplicationArgs appArgs){
        this.appArgs = appArgs;
        this.stdin = new BufferedReader(new InputStreamReader(System.in, Charsets.getDefault()));
    }

    @Override
    public String[] get() {
        if(!appArgsUsed && this.appArgs!=null) {
            this.appArgsUsed = true;
            return this.appArgs.getArgs();
        }
        String line = null;
        try {
            line = this.stdin.readLine();
            line = line.trim();
            if(line.isEmpty()){
                return Emptys.EMPTY_STRINGS;
            }
            if( line.endsWith(";") || !Strings.endsWith(line,"\\")){
                return Collects.toArray(cmdlineTokenizer.tokenize(), String[].class);
            }
            // 多行模式下
            StringBuilder buffer= new StringBuilder(255);
            buffer.append(line);

            line = this.stdin.readLine();
            while (!lineEnd(line)){
                buffer.append(" " +line);
            }
            buffer.append(line);
        }catch (IOException ioe){
            return Collects.toArray(cmdlineTokenizer.tokenize(), String[].class);
        }
        return null;
    }

    private boolean lineEnd(String lineFragment){
        return lineFragment.isEmpty() || lineFragment.endsWith(";") || !Strings.endsWith(lineFragment,"\\");
    }
}
