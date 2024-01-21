package com.jn.agileway.test.util.os.hardware.cpu;

import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.io.IOs;

import java.io.IOException;
import java.util.List;

class LinuxGetCpuIdStreamHandler extends GetCpuIdStreamHandler {

    @Override
    public void start() throws IOException {
        outputContent.reset();
        if (this.subProcessOutputStream != null) {
            List<String> lines = IOs.readLines(this.subProcessOutputStream);
            String cpuId = Collects.findFirst(lines, new Predicate<String>() {
                @Override
                public boolean test(String line) {
                    return Strings.isNotBlank(line);
                }
            });
            if (Strings.startsWith(cpuId, "ID:")) {
                cpuId = cpuId.substring("ID:".length());
            }
            if (Emptys.isNotEmpty(cpuId)) {
                outputContent.set(cpuId.trim());
            }
        }
    }

    @Override
    public String getCpuId() {
        return outputContent.get();
    }
}
