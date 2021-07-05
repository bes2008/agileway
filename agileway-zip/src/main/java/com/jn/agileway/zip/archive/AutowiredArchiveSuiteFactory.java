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
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class AutowiredArchiveSuiteFactory implements ArchiveSuiteFactory, Registry<String, SingleArchiveSuiteFactory> {
    private static final Logger logger = LoggerFactory.getLogger(AutowiredArchiveSuiteFactory.class);
    private Map<String, SingleArchiveSuiteFactory> archiverFactoryMap = new ConcurrentHashMap<String, SingleArchiveSuiteFactory>();

    private AutowiredArchiveSuiteFactory() {
        Collects.forEach(ServiceLoader.load(SingleArchiveSuiteFactory.class), new Consumer<SingleArchiveSuiteFactory>() {
            @Override
            public void accept(SingleArchiveSuiteFactory archiverFactory) {
                archiverFactoryMap.put(archiverFactory.getArchiveFormat(), archiverFactory);
            }
        });
    }

    private static final AutowiredArchiveSuiteFactory INSTANCE = new AutowiredArchiveSuiteFactory();

    public static AutowiredArchiveSuiteFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public void register(SingleArchiveSuiteFactory archiverFactory) {
        register(archiverFactory.getArchiveFormat(), archiverFactory);
    }

    @Override
    public void register(String s, SingleArchiveSuiteFactory archiverFactory) {
        archiverFactoryMap.put(s, archiverFactory);
    }

    @Override
    public SingleArchiveSuiteFactory get(String format) {
        SingleArchiveSuiteFactory factory = null;
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
                    if (!(outputStream instanceof BufferedOutputStream)) {
                        outputStream = new BufferedOutputStream(outputStream);
                    }
                    outputStream = new CompressorStreamFactory().createCompressorOutputStream(zipFormat.getCompress(), outputStream);
                } catch (Throwable ex) {
                    throw Throwables.wrapAsRuntimeException(ex);
                }
            }
        }
        SingleArchiveSuiteFactory factory = get(format);
        return factory.get(zipFormat.getArchive(), outputStream);
    }

    @Override
    public Expander get(String format, InputStream inputStream) {
        ZipFormat zipFormat = ZipFormats.getZipFormat(format);
        Preconditions.checkNotNull(zipFormat);
        Preconditions.checkTrue(zipFormat.isValid());
        if (Strings.isNotEmpty(zipFormat.getCompress())) {
            if (!(inputStream instanceof CompressorInputStream)) {
                try {
                    if (!(inputStream instanceof BufferedInputStream)) {
                        inputStream = new BufferedInputStream(inputStream);
                    }
                    inputStream = new CompressorStreamFactory().createCompressorInputStream(zipFormat.getCompress(), inputStream);
                } catch (Throwable ex) {
                    throw Throwables.wrapAsRuntimeException(ex);
                }
            }
        }
        SingleArchiveSuiteFactory factory = get(format);
        return factory.get(zipFormat.getArchive(), inputStream);
    }
}
