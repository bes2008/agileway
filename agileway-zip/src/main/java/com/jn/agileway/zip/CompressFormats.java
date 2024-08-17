package com.jn.agileway.zip;

import com.jn.langx.io.resource.Resource;
import com.jn.langx.io.resource.Resources;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.spi.CommonServiceProvider;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CompressFormats {
    private static final Logger logger = Loggers.getLogger(CompressFormats.class);

    private CompressFormats() {
    }

    /**
     * key: ZipFormat#getFormat()
     */
    private static Map<String, CompressFormat> registry = new ConcurrentHashMap<String, CompressFormat>();
    private static Set<String> supportedFormats = new TreeSet<String>(new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            if (o1.equals(o2)) {
                return 0;
            }
            if (Strings.startsWith(o1, o2)) {
                return -1;
            }
            if (Strings.startsWith(o2, o1)) {
                return 1;
            }

            return o2.compareTo(o1);
        }
    });

    static {
        loadBuiltinZipFormats();
        loadCustomizedZipFormats();
    }

    private static void loadBuiltinZipFormats() {
        Resource resource = Resources.loadClassPathResource("/compressformats.yml");
        InputStream stream = null;
        try {
            stream = resource.getInputStream();
            Iterable<Object> objs = new Yaml().loadAll(stream);
            Collects.forEach(objs, new Consumer<Object>() {
                @Override
                public void accept(Object o0) {
                    if (o0 instanceof List) {
                        Collects.forEach((List) o0, new Consumer<Object>() {
                            @Override
                            public void accept(Object o) {
                                Map<String, String> map = (Map<String, String>) o;
                                CompressFormat zipFormat = new CompressFormat();
                                if (Strings.isNotEmpty(map.get("format"))) {
                                    zipFormat.setFormat(map.get("format"));
                                    zipFormat.setArchive(map.get("archive"));
                                    zipFormat.setDesc(map.get("desc"));
                                    zipFormat.setUncompressSuffix(map.get("uncompressSuffix"));
                                    zipFormat.setCompress(map.get("compress"));
                                    addZipFormat(zipFormat);
                                }
                            }
                        });

                    }
                }
            });
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private static void loadCustomizedZipFormats() {
        Collects.forEach(CommonServiceProvider.loadService(CompressFormatFactory.class), new Consumer<CompressFormatFactory>() {
            @Override
            public void accept(CompressFormatFactory zipFormatFactory) {
                List<CompressFormat> zfs = zipFormatFactory.get();
                Collects.forEach(zfs, new Consumer<CompressFormat>() {
                    @Override
                    public void accept(CompressFormat zipFormat) {
                        addZipFormat(zipFormat);
                    }
                });
            }
        });
    }

    public static void addZipFormat(CompressFormat format) {
        if (format.isValid()) {
            registry.put(format.getFormat(), format);
            supportedFormats.add(format.getFormat());
        }
    }

    public static CompressFormat getZipFormat(String format) {
        return registry.get(format);
    }

    public static boolean archiveEnabled(String format) {
        CompressFormat zf = getZipFormat(format);
        return zf != null && zf.archiveEnabled();
    }

    public static String getArchiveFormat(String format) {
        CompressFormat zf = getZipFormat(format);
        return zf != null ? zf.getArchive() : null;
    }

    public static Set<String> getSupportedFormats() {
        return supportedFormats;
    }

    public static String getFormat(final String filepath) {
        Set<String> formats = getSupportedFormats();
        String format = Collects.findFirst(formats, new Predicate<String>() {
            @Override
            public boolean test(String fmt) {
                return Strings.endsWith(filepath, "." + fmt);
            }
        });
        return format;
    }
}
