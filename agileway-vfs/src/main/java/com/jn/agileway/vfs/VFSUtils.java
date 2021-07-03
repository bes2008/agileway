package com.jn.agileway.vfs;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.reflect.Reflects;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.FileProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;


public class VFSUtils {
    private static final Logger logger = LoggerFactory.getLogger(VFSUtils.class);

    public static DefaultFileSystemManager getAsDefaultFSManager(FileSystemManager fileSystemManager) {
        if (fileSystemManager instanceof DefaultFileSystemManager) {
            return (DefaultFileSystemManager) fileSystemManager;
        }
        throw new IllegalArgumentException("Not a DefaultFileSystemManager");
    }

    private final static Field defaultFileSystemManager_providers = Reflects.getDeclaredField(DefaultFileSystemManager.class, "providers");
    private final static Field defaultFileSystemManager_defaultProvider = Reflects.getDeclaredField(DefaultFileSystemManager.class, "defaultProvider");
    private final static Field defaultFileSystemManager_components = Reflects.getDeclaredField(DefaultFileSystemManager.class, "components");

    public static Map<String, FileProvider> getAllProviders(final DefaultFileSystemManager fileSystemManager) {
        return Reflects.getFieldValue(defaultFileSystemManager_providers, fileSystemManager, true, false);
    }

    public static void removeFileProvider(final DefaultFileSystemManager fileSystemManager, String uriSchema) {
        if (fileSystemManager.hasProvider(uriSchema)) {
            FileProvider provider = getAllProviders(fileSystemManager).remove(uriSchema);
            if (provider != null) {
                FileProvider defaultProvider = Reflects.getFieldValue(defaultFileSystemManager_defaultProvider, fileSystemManager, true, false);
                if (defaultProvider == provider) {
                    Reflects.setFieldValue(defaultFileSystemManager_defaultProvider, fileSystemManager, null, true, false);
                }
                List<Object> components = Reflects.getFieldValue(defaultFileSystemManager_components, fileSystemManager, true, false);
                components.remove(provider);
            }
        }
    }

    public static void addFileProvider(final DefaultFileSystemManager fileSystemManager, final FileProvider provider, String... uriSchemas) {
        Collects.forEach(uriSchemas, new Consumer<String>() {
            @Override
            public void accept(String uriSchema) {
                removeFileProvider(fileSystemManager, uriSchema);

                try {
                    fileSystemManager.addProvider(uriSchema, provider);
                } catch (FileSystemException ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        });
    }


}
