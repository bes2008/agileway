package com.jn.agileway.zip.archive;

import com.jn.agileway.zip.format.ZipFormat;
import com.jn.agileway.zip.format.ZipFormats;
import com.jn.langx.annotation.Singleton;
import com.jn.langx.registry.Registry;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class AutowiredArchiverFactory implements ArchiverFactory, Registry<String, ArchiverFactory> {
    private static final Logger logger = LoggerFactory.getLogger(AutowiredArchiverFactory.class);
    private Map<String, ArchiverFactory> archiverFactoryMap = new ConcurrentHashMap<String, ArchiverFactory>();

    private AutowiredArchiverFactory() {
        Collects.forEach(ServiceLoader.load(ArchiverFactory.class), new Consumer<ArchiverFactory>() {
            @Override
            public void accept(ArchiverFactory archiverFactory) {
                archiverFactoryMap.put(archiverFactory.getArchiveFormat(), archiverFactory);
            }
        });
    }

    private static final AutowiredArchiverFactory INSTANCE = new AutowiredArchiverFactory();

    public static AutowiredArchiverFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public void register(ArchiverFactory archiverFactory) {
        register(archiverFactory.getArchiveFormat(), archiverFactory);
    }

    @Override
    public void register(String s, ArchiverFactory archiverFactory) {
        archiverFactoryMap.put(s, archiverFactory);
    }

    @Override
    public ArchiverFactory get(String format) {
        ArchiverFactory factory = null;
        String archive = ZipFormats.getArchiveFormat(format);
        if (Strings.isNotEmpty(archive)) {
            factory = archiverFactoryMap.get(archive);
            if (factory == null) {
                factory = archiverFactoryMap.get("simple");
            }
        }
        return factory;
    }

    @Override
    public Archiver get(String format, OutputStream outputStream) {
        ZipFormat zipFormat = ZipFormats.getZipFormat(format);
        Preconditions.checkNotNull(zipFormat);
        Preconditions.checkTrue(zipFormat.isValid());
        if (Strings.isNotEmpty(zipFormat.getCompress())) {
            if (!(outputStream instanceof CompressorOutputStream)) {
                try {
                    outputStream = new CompressorStreamFactory().createCompressorOutputStream(zipFormat.getCompress(), new BufferedOutputStream(outputStream));
                } catch (Throwable ex) {
                    throw Throwables.wrapAsRuntimeException(ex);
                }
            }
        }
        ArchiverFactory factory = get(format);
        return factory.get(zipFormat.getArchive(), outputStream);
    }

    @Override
    public String getArchiveFormat() {
        return "autowired";
    }
}
