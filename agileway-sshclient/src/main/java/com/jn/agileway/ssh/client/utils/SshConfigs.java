package com.jn.agileway.ssh.client.utils;


import com.jn.agileway.ssh.client.AbstractSshConnectionConfig;
import com.jn.langx.util.Strings;
import com.jn.langx.util.SystemPropertys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.io.file.Files;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SshConfigs {
    private static final Logger logger = Loggers.getLogger(SshConfigs.class);

    public static List<File> getKnownHostsFiles(String _paths) {
        return getKnownHostsFiles(_paths, true, true);
    }

    public static List<File> getKnownHostsFiles(String _paths, final boolean filterNotExist, boolean mkIfDefaultNotExist) {
        final List<File> files = Collects.emptyArrayList();
        if (Strings.isNotBlank(_paths)) {
            String[] paths = Strings.split(_paths, ";");
            Collects.forEach(paths, new Consumer<String>() {
                @Override
                public void accept(String path) {
                    if (Strings.startsWith(path, "~")) {
                        path = "${user.home}" + Strings.substring(path, 1);
                    }
                    path = path.replace("${user.home}", SystemPropertys.getUserHome().replace("\\", "/"));

                    File file = new File(path);
                    if (file.exists()) {
                        files.add(file);
                    } else {
                        boolean makeAndAdd = false;
                        if (filterNotExist) {
                            boolean isDefaultPath = AbstractSshConnectionConfig.KNOWN_HOSTS_PATH_DEFAULT.equals(path);
                            makeAndAdd = isDefaultPath;
                        } else {
                            makeAndAdd = true;
                        }
                        if (makeAndAdd) {
                            try {
                                Files.makeFile(file);
                            } catch (IOException ex) {
                                logger.warn(ex.getMessage(), ex);
                            }
                            if (file.exists()) {
                                files.add(file);
                            }
                        }
                    }

                }
            });
        }
        return files;
    }
}
