package com.jn.agileway.zip.format;

import com.jn.langx.io.resource.Resource;
import com.jn.langx.io.resource.Resources;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ZipFormats {
    private static final Logger logger = Loggers.getLogger(ZipFormats.class);

    private ZipFormats() {
    }

    /**
     * key: ZipFormat#getFormat()
     */
    private static Map<String, ZipFormat> registry = new ConcurrentHashMap<String, ZipFormat>();
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
        Resource resource = Resources.loadClassPathResource("/zipformats.yml");
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
                                ZipFormat zipFormat = new ZipFormat();
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
        Collects.forEach(ServiceLoader.load(ZipFormatFactory.class), new Consumer<ZipFormatFactory>() {
            @Override
            public void accept(ZipFormatFactory zipFormatFactory) {
                List<ZipFormat> zfs = zipFormatFactory.get();
                Collects.forEach(zfs, new Consumer<ZipFormat>() {
                    @Override
                    public void accept(ZipFormat zipFormat) {
                        addZipFormat(zipFormat);
                    }
                });
            }
        });
    }

    public static void addZipFormat(ZipFormat format) {
        if (format.isValid()) {
            registry.put(format.getFormat(), format);
            supportedFormats.add(format.getFormat());
        }
    }

    public static ZipFormat getZipFormat(String format) {
        return registry.get(format);
    }

    public static boolean archiveEnabled(String format) {
        ZipFormat zf = getZipFormat(format);
        return zf != null && zf.archiveEnabled();
    }

    public static String getArchiveFormat(String format) {
        ZipFormat zf = getZipFormat(format);
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
