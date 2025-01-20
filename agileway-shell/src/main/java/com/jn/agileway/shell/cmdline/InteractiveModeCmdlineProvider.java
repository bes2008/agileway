package com.jn.agileway.shell.cmdline;

import com.jn.agileway.shell.ApplicationArgs;
import com.jn.agileway.shell.ShellLines;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.Charsets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InteractiveModeCmdlineProvider implements CmdlineProvider{
    private ApplicationArgs appArgs;
    private boolean appArgsUsed = false;
    private BufferedReader stdin;

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

            if(Strings.startsWith(line, "//")){
                // this is a comment line, ignore it
                line = "";
            }
            if(Strings.startsWith(line, "/")){
                line = Strings.substring(line, 1);
            }
            if(line.isEmpty()){
                return ShellLines.cmdlineToArgs(line);
            }
            if( line.endsWith(";") || !Strings.endsWith(line,"\\")){
                return ShellLines.cmdlineToArgs(line);
            }
            // 多行模式下
            StringBuilder buffer= new StringBuilder(255);
            buffer.append(Strings.substring(line,0, line.length()-1)).append(Strings.SPACE);

            line = this.stdin.readLine();
            while (!lineEnd(line)){
                buffer.append(Strings.substring(line,0, line.length()-1)).append(Strings.SPACE);
                line = this.stdin.readLine();
                if(Strings.startsWith(line, "//")){
                    // this is a comment line
                    line="";
                    break;
                }
            }
            buffer.append(line);
            String rawLine = buffer.toString();
            if(Strings.endsWith(rawLine, "\\")){
                System.out.println("cmd error");
                return Emptys.EMPTY_STRINGS;
            }
            return ShellLines.cmdlineToArgs(line);
        }catch (IOException ioe){
            return null;
        }
    }

    private boolean lineEnd(String lineFragment){
        return lineFragment.isEmpty() || lineFragment.endsWith(";") || !Strings.endsWith(lineFragment,"\\");
    }
}
