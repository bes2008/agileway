package com.jn.agileway.ssh.client.channel;

import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.SshException;
import com.jn.langx.io.stream.ByteArrayOutputStream;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Maths;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.function.Supplier;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.struct.Holder;

import java.io.*;

public class DefaultShellExecutor extends AbstractInitializable implements ShellExecutor {
    private SessionedChannel channel;

    public DefaultShellExecutor(final SshConnection connection){
        this(new Supplier<SshConnection, SessionedChannel>() {
            @Override
            public SessionedChannel get(SshConnection conn) {
                SessionedChannel channel = connection.openSession();
                channel.pty("vt100");
                channel.shell();
                return channel;
            }
        }.get(connection));
    }
    
    public DefaultShellExecutor(SessionedChannel channel){
        Preconditions.checkArgument(channel.getType()==ChannelType.SHELL);
        this.channel = channel;
    }

    @Override
    public SessionedChannel getChannel() {
        return channel;
    }

    @Override
    protected void doInit() throws InitializationException {
        // 先将刚连上时的输出读取出来
        Holder<String> stdout = new Holder<String>();
        Holder<String> stderr = new Holder<String>();
        String shellInitLines = null;
        try {
            boolean readInitLinesSuccess = readOutputs(stdout, stderr, null, null, 500, 3);
            shellInitLines = readInitLinesSuccess ? stdout.get() : stderr.get();
        }catch (IOException e){
            Loggers.getLogger(DefaultShellExecutor.class).error("error when read shell ");
            throw new SshException(e);
        }
        Loggers.getLogger(DefaultShellExecutor.class).info("shell init output lines: \n{}", Objs.useValueIfEmpty(shellInitLines,""));

    }

    @Override
    public boolean execute(String statementBlock, String moreFlagLine, String more, long responseTimeInMills, int maxAttempts, Holder<String> stdout, Holder<String> stderr) {
        if(!inited){
            init();
        }
        stderr.reset();
        stdout.reset();
        try {
            OutputStream outputStream = getChannel().getOutputStream();
            more = Objs.useValueIfEmpty(more, " ");
            moreFlagLine = Objs.useValueIfEmpty(more, "---- More ----");
            responseTimeInMills = Maths.maxLong(100, responseTimeInMills);
            outputStream.write((statementBlock + Strings.CRLF).getBytes(Charsets.UTF_8));
            outputStream.flush();
            return readOutputs(stdout, stderr, moreFlagLine, more, responseTimeInMills, maxAttempts);
        }catch (Throwable e){
            throw new SshException(e);
        }
    }

    private boolean readOutputs(Holder<String> stdout, Holder<String> stderr,String moreFlagLine, String more, long responseTimeInMills, int maxAttempts) throws IOException {
        byte[] buffer = new byte[IOs.DEFAULT_BUFFER_SIZE];
        OutputStream outputStream = getChannel().getOutputStream();
        InputStream stdin = getChannel().getInputStream();
        InputStream errIn = channel.getErrorInputStream();
        ByteArrayOutputStream resultOutputStream = new ByteArrayOutputStream();
        boolean hasError = false;
        // 剩余的尝试次数
        int attempts = maxAttempts;
        while (true){
            InputStream in = null;
            if (errIn.available()>0){
                in = errIn;
            } else if (stdin.available()>0) {
                in = stdin;
            }else{
                attempts--;
            }

            if(in!=null){
                // 先对buffer扩容
                if(in.available()>buffer.length){
                    int newBufferSize = (in.available() / buffer.length +1) *buffer.length;
                    buffer=new byte[newBufferSize];
                }
                // 一次性全部读出
                int length = in.read(buffer,0, buffer.length);
                // EOF
                if(length<0){
                    break;
                }

                String s = new String(buffer, 0, length);
                // TODO 找到最后一行，如果是moreFlagLine就移除
                if (s.contains(moreFlagLine)) {
                    outputStream.write(more.getBytes(Charsets.UTF_8));
                    outputStream.flush();
                }
                // TODO 将 moreFlagLine就移除 移除后的内容加入到 resultOutputStream
                resultOutputStream.write(buffer,0,length);
                attempts=maxAttempts;
            }

            if(attempts<=0){
                break;
            }
            try {
                Thread.sleep(responseTimeInMills);
            }catch (Throwable e){
                // ignore it
            }
        }

        if(hasError){
            stderr.set(resultOutputStream.toString(Charsets.UTF_8));
        }else{
            stdout.set(resultOutputStream.toString(Charsets.UTF_8));
        }

        return !hasError;
    }
}
