package com.jn.agileway.ssh.client.channel;

import com.jn.langx.util.struct.Holder;

public interface ShellExecutor {
    SessionedChannel getChannel();
    /**
     * @param statementBlock 要执行的shell 语句块
     * @param moreFlagLine 还有更多输出时的标志行
     * @param responseTime 从stdout, stderr读取结果时，最大尝试读取不到数据时间
     * @param maxAttempts 从stdout, stderr读取结果时，最大尝试次数
     * @param stdout 执行成功时的
     * @param stderr 执行失败时的错误内容
     * @return 执行是否执行成功
     */
    boolean execute(String statementBlock, String moreFlagLine, String more, long responseTime, int maxAttempts, Holder<String> stdout, Holder<String> stderr);
}
