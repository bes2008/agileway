package com.jn.agileway.ssh.client.impl.jsch.exec;

import com.jcraft.jsch.Session;
import com.jn.langx.commandline.DefaultCommandLineExecutor;
import com.jn.langx.util.Objs;

import java.io.File;

public class JschCommandLineExecutor extends DefaultCommandLineExecutor {

    public JschCommandLineExecutor(Session session) {
        this(null, session);
    }

    public JschCommandLineExecutor(File workingDirectory, Session session) {
        setWorkingDirectory(Objs.useValueIfNull(workingDirectory, new File("~")));
        setLauncher(new JschCommandLineLauncher(session));
    }

}
