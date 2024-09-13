package com.jn.agileway.zip.archive;

import com.jn.agileway.zip.CompressFormat;
import com.jn.agileway.zip.CompressFormats;
import com.jn.agileway.zip.UnsupportedArchiveFormatException;
import com.jn.langx.annotation.Singleton;
import com.jn.langx.registry.Registry;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.spi.CommonServiceProvider;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class AutowiredArchiveSuiteFactory implements ArchiveSuiteFactory, Registry<String, SingleArchiveSuiteFactory> {
    private Map<String, SingleArchiveSuiteFactory> archiveFactoryMap = new ConcurrentHashMap<String, SingleArchiveSuiteFactory>();

    private AutowiredArchiveSuiteFactory() {
        Collects.forEach(CommonServiceProvider.loadService(SingleArchiveSuiteFactory.class), new Consumer<SingleArchiveSuiteFactory>() {
            @Override
            public void accept(SingleArchiveSuiteFactory archiverFactory) {
                archiveFactoryMap.put(archiverFactory.getArchiveFormat(), archiverFactory);
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
        archiveFactoryMap.put(s, archiverFactory);
    }

    @Override
    public void unregister(String key) {
        archiveFactoryMap.remove(key);
    }

    @Override
    public boolean contains(String key) {
        return archiveFactoryMap.containsKey(key);
    }

    @Override
    public SingleArchiveSuiteFactory get(String format) {
        SingleArchiveSuiteFactory factory = null;
        String archive = CompressFormats.getArchiveFormat(format);
        if (Strings.isNotEmpty(archive)) {
            factory = archiveFactoryMap.get(archive);
            if (factory == null) {
                factory = archiveFactoryMap.get("simple");
            }
        }
        return factory;
    }

    @Override
    public Archiver get(String format, OutputStream outputStream) {
        CompressFormat compressFormat = CompressFormats.getZipFormat(format);
        Preconditions.checkNotNull(compressFormat);
        if (!compressFormat.isValid()) {
            throw new UnsupportedArchiveFormatException(StringTemplates.formatWithPlaceholder("unsupported archive format: {}", format));
        }
        if (compressFormat.compressEnabled()) {
            if (!(outputStream instanceof CompressorOutputStream)) {
                try {
                    if (!(outputStream instanceof BufferedOutputStream)) {
                        outputStream = new BufferedOutputStream(outputStream);
                    }
                    outputStream = new CompressorStreamFactory().createCompressorOutputStream(compressFormat.getCompress(), outputStream);
                } catch (Throwable ex) {
                    throw Throwables.wrapAsRuntimeException(ex);
                }
            }
        }
        SingleArchiveSuiteFactory factory = get(format);
        return factory.get(compressFormat.getArchive(), outputStream);
    }

    @Override
    public Expander get(String format, InputStream inputStream) {
        CompressFormat zipFormat = CompressFormats.getZipFormat(format);
        Preconditions.checkNotNull(zipFormat);
        if (!zipFormat.isValid()) {
            throw new UnsupportedArchiveFormatException(StringTemplates.formatWithPlaceholder("unsupported archive format: {}", format));
        }
        if (zipFormat.compressEnabled()) {
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

    public Archiver createArchiver(File outFile) {
        final String path = outFile.getPath();
        String format = CompressFormats.getFormat(path);
        if (format == null) {
            throw new UnsupportedArchiveFormatException(StringTemplates.formatWithPlaceholder("Can't find a suite archive format for file: {}", path));
        }
        try {
            Archiver archiver = get(format, new FileOutputStream(outFile));
            archiver.setFilepath(outFile.getPath());
            return archiver;
        } catch (IOException ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }

    public Archiver createArchiver(String outFilepath) {
        return createArchiver(new File(outFilepath));
    }

    public Expander createExpander(String inFilepath) {
        return createExpander(new File(inFilepath));
    }

    public Expander createExpander(File inFile) {
        final String path = inFile.getPath();
        String format = CompressFormats.getFormat(path);
        if (format == null) {
            throw new UnsupportedArchiveFormatException(StringTemplates.formatWithPlaceholder("Can't find a suite archive format for file: {}", path));
        }
        try {
            Expander expander = get(format, new FileInputStream(inFile));
            expander.setFilepath(inFile.getAbsolutePath());
            return expander;
        } catch (IOException ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }
}
