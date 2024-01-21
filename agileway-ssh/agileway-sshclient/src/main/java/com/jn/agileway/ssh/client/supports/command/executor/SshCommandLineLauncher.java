package com.jn.agileway.ssh.client.supports.command.executor;

import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.SessionedChannel;
import com.jn.agileway.cmd.CommandLine;
import com.jn.agileway.cmd.launcher.CommandLauncher;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Supplier;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class SshCommandLineLauncher implements CommandLauncher<SshCommandExecutionAdaptor> {
    private SshConnection connection;
    private Supplier<Map<String, String>, String> environmentSettingsSupplier;


    public SshCommandLineLauncher(SshConnection connection) {
        this.connection = connection;
    }

    public void setEnvironmentSettingsSupplier(Supplier<Map<String, String>, String> environmentSettingsSupplier) {
        this.environmentSettingsSupplier = environmentSettingsSupplier;
    }

    @Override
    public SshCommandExecutionAdaptor exec(CommandLine commandLine, Map<String, String> env) throws IOException {
        return exec(commandLine, env, null);
    }

    @Override
    public SshCommandExecutionAdaptor exec(CommandLine commandLine, Map<String, String> environmentVariables, File workingDirectory) throws IOException {
        try {
            if (!connection.isConnected()) {
                throw new SshException(new IllegalStateException("connection is not connected"));
            }

            final SessionedChannel sessionChannel = connection.openSession();
            Preconditions.checkNotNull(sessionChannel, "the ssh exec session channel is null");
            String command = commandLine.getCommandLineString();

            if (workingDirectory != null) {
                String path = workingDirectory.getPath();
                path = path.replace("\\", "/");
                command = "cd " + path + ";" + command;
            }

            if (Emptys.isNotEmpty(environmentVariables)) {
                String envs = null;
                if (environmentSettingsSupplier != null) {
                    envs = environmentSettingsSupplier.get(environmentVariables);
                }
                if (Strings.isNotEmpty(envs)) {
                    command = envs + command;
                } else {
                    Collects.forEach(environmentVariables, new Consumer2<String, String>() {
                        @Override
                        public void accept(String variable, String value) {
                            sessionChannel.env(variable, value);
                        }
                    });
                }
            }

            sessionChannel.exec(command);
            return new SshCommandExecutionAdaptor(sessionChannel);
        } catch (Throwable ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }

    @Override
    public boolean isFailure(int i) {
        return false;
    }
}
