package com.jn.agileway.cmd;

import java.io.InputStream;
import java.io.OutputStream;

public interface InstructionSequence {

    OutputStream getOutputStream();

    InputStream getInputStream();

    InputStream getErrorStream();

    void destroy();

    int waitFor() throws InterruptedException;

    int exitValue();
}
