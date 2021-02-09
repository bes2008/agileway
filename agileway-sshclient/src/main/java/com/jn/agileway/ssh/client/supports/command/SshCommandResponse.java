package com.jn.agileway.ssh.client.supports.command;

import com.jn.langx.util.Strings;

public class SshCommandResponse {
    /**
     * error 输出
     */
    private String exitErrorMessage;

    /**
     * 标准输出内容
     */
    private String result;

    /**
     * -1 为没有设置
     */
    private int exitStatus = -1;

    public SshCommandResponse() {
    }

    public SshCommandResponse(int exitStatus, String result, String error) {
        setExitErrorMessage(error);
        setExitStatus(exitStatus);
        setResult(result);
    }

    public boolean hasError() {
        if (exitStatus < 0) {
            return Strings.isNotEmpty(exitErrorMessage);
        }
        return exitStatus > 0;
    }

    public String getExitErrorMessage() {
        return exitErrorMessage;
    }

    public void setExitErrorMessage(String exitErrorMessage) {
        this.exitErrorMessage = exitErrorMessage;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getExitStatus() {
        return exitStatus;
    }

    public void setExitStatus(int exitStatus) {
        this.exitStatus = exitStatus;
    }
}
