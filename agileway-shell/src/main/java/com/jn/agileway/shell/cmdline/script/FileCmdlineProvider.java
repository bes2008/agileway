package com.jn.agileway.shell.cmdline.script;

import com.jn.agileway.shell.ApplicationArgs;
import com.jn.agileway.shell.cmdline.InputStreamCmdlineProvider;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.io.Charsets;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

public class FileCmdlineProvider extends InputStreamCmdlineProvider {

    private String filepath;
    public FileCmdlineProvider(String filepath){
        this(null, filepath);
    }
    public FileCmdlineProvider(ApplicationArgs applicationArgs, String filepath){
        super(applicationArgs);
        this.filepath = filepath;
        try {
            this.reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.filepath), Charsets.getDefault()));
        }catch (FileNotFoundException e){
            throw Throwables.wrapAsRuntimeIOException(e);
        }

    }

}