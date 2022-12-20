package com.jn.agileway.ssh.client;

import com.jn.agileway.ssh.client.channel.SessionedChannel;
import com.jn.agileway.ssh.client.supports.command.SshCommandResponse;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Supplier;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

public class SshClients {
    private static final Logger logger = Loggers.getLogger(SshClients.class);

    public static SshCommandResponse exec(@NonNull SshConnection connection, @NonNull String command) throws SshException {
        return exec(connection, null, null, command, null);
    }

    /**
     * 通过ssh 连接到远程机器上，cd到指定的workingDirectory下， 执行命令
     *
     * @param connection
     * @param environmentVariables 环境变量
     * @param workingDirectory     工作目录
     * @param command              要执行的命令
     * @param encoding             输出内容的编码，默认为 UTF-8
     * @return
     * @throws SshException
     */
    public static SshCommandResponse exec(@NonNull SshConnection connection, @Nullable Map<String, String> environmentVariables, @Nullable String workingDirectory, @NonNull String command, @Nullable String encoding) throws SshException{
        return exec(connection, environmentVariables, null, workingDirectory, command, encoding);
    }

    public static SshCommandResponse exec(@NonNull SshConnection connection, @Nullable Map<String, String> environmentVariables, @Nullable Supplier<Map<String, String>, String> environmentSettingsSupplier, @Nullable String workingDirectory, @NonNull String command, @Nullable String encoding) throws SshException {
        Preconditions.checkState(connection != null && connection.isConnected() && !connection.isClosed(), "connection status invalid");
        Preconditions.checkNotEmpty(command, "the command is not supplied");
        Charset charset = Charsets.UTF_8;
        if (Strings.isNotEmpty(encoding)) {
            try {
                charset = Charsets.getCharset(encoding);
            } catch (Throwable ex) {
                logger.warn("The encoding is invalid : {}", encoding);
            }
        }

        final SessionedChannel channel = connection.openSession();


        if (Strings.isNotEmpty(workingDirectory)) {
            workingDirectory = workingDirectory.replace("\\", "/");
            command = "cd " + workingDirectory + ";" + command;
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
                        channel.env(variable, value);
                    }
                });
            }
        }

        channel.exec(command);
        int exitStatus = channel.getExitStatus();

        SshCommandResponse response = new SshCommandResponse();
        response.setExitStatus(exitStatus);
        try {
            if (exitStatus != 0) {
                InputStream errorInputStream = channel.getErrorInputStream();
                byte[] errorContent = IOs.toByteArray(errorInputStream);
                String error = new String(errorContent, charset);
                response.setExitErrorMessage(error);
            } else {
                InputStream inputStream = channel.getInputStream();
                //    if (inputStream.available() > 0) { // 这行代码可以避免阻塞，但又容易导致某些ssh 库获取不到任何内容
                byte[] bytes = IOs.toByteArray(inputStream);
                String content = new String(bytes, charset);
                response.setResult(content);
                //    }
            }
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        }
        return response;
    }

    /**
     * 获取当前用户的 user id
     *
     * @param connection
     */
    public static int getUid(SshConnection connection) {
        SshCommandResponse response = SshClients.exec(connection, "id -u");
        if (!response.hasError()) {
            return Integer.parseInt(response.getResult());
        }
        return -1;
    }

    /**
     * 获取当前用户所属的Group的id集
     *
     * @param connection
     */
    public static int[] getGroupIds(SshConnection connection) {
        SshCommandResponse response = SshClients.exec(connection, "id -G");
        if (!response.hasError()) {
            final String[] groups = response.getResult().trim().split("\\s+");

            final int[] groupsIds = new int[groups.length];
            for (int i = 0; i < groups.length; i++) {
                groupsIds[i] = Integer.parseInt(groups[i]);
            }
            return groupsIds;
        }
        return null;
    }

}
