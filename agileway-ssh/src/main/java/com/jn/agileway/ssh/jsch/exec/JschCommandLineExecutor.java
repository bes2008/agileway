package com.jn.agileway.ssh.jsch.exec;

import com.jn.langx.commandline.DefaultCommandLineExecutor;

import java.io.File;

public class JschCommandLineExecutor extends DefaultCommandLineExecutor {
    public JschCommandLineExecutor() {
        // home dir
        File homeDir = new File("~");
        setWorkingDirectory(homeDir);
    }
}
