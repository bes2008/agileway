package com.jn.agileway.shell.cmdline.interactive.jline2;

import com.jn.agileway.shell.cmdline.CmdlineReader;
import com.jn.agileway.shell.cmdline.ShellCmdlines;
import com.jn.langx.util.Throwables;
import jline.console.ConsoleReader;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

public class Jline2CmdlineReader implements CmdlineReader {

    private ConsoleReader jlineReader;
    public Jline2CmdlineReader(){
        try {
            ConsoleReader console = new ConsoleReader("agileway-shell", new FileInputStream(FileDescriptor.in), System.out, null);
            this.jlineReader = console;
        }catch (IOException e){
            throw Throwables.wrapAsRuntimeIOException(e);
        }
    }
    @Override
    public void setPrompt(String prompt) {
        this.jlineReader.setPrompt(prompt);
    }

    @Override
    public String[] readCmdline() {
        try {
            String line = this.jlineReader.readLine();
            return ShellCmdlines.cmdlineToArgs(line);
        }catch (IOException e){
            return null;
        }
    }

    @Override
    public void close() throws IOException {
        this.jlineReader.close();
    }
}
