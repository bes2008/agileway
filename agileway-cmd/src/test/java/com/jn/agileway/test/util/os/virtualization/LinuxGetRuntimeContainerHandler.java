package com.jn.agileway.test.util.os.virtualization;

import com.jn.langx.util.io.IOs;

import java.io.IOException;

class LinuxGetRuntimeContainerHandler extends GetRuntimeContainerHandler {

    public RuntimeContainer getContainer() {
        return (RuntimeContainer) outputContent.get();
    }

    @Override
    public void start() throws IOException {
        this.outputContent.reset();
        String content = IOs.readAsString(this.subProcessOutputStream);
        content = content.trim();

        // 没有在容器下
        if ("/".equals(content)) {
            return;
        }

        // docker
        if (content.startsWith("/docker/")) {
            outputContent.set(new RuntimeContainer("docker"));
        }
    }
}
