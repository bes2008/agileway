package com.jn.agileway.ssh.client.utils;


import com.jn.langx.util.Strings;
import com.jn.langx.util.SystemPropertys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;

import java.io.File;
import java.util.List;

public class SshConfigs {
    public static List<File> getKnownHostsFiles(String _paths) {
        return getKnownHostsFiles(_paths, true);
    }

    public static List<File> getKnownHostsFiles(String _paths, final boolean filterNotExist) {
        final List<File> files = Collects.emptyArrayList();
        if (Strings.isNotBlank(_paths)) {
            String[] paths = Strings.split(_paths, ";");
            Collects.forEach(paths, new Consumer<String>() {
                @Override
                public void accept(String path) {
                    path = path.replace("${user.home}", SystemPropertys.getUserHome().replace("\\","/"));
                    File file = new File(path);
                    if (filterNotExist) {
                        if (file.exists()) {
                            files.add(file);
                        }
                    }else{
                        files.add(file);
                    }
                }
            });
        }
        return files;
    }
}
