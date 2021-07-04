package com.jn.agileway.zip.format;

import com.jn.langx.io.resource.Resource;
import com.jn.langx.io.resource.Resources;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZipFormats {
    private static final Logger logger = LoggerFactory.getLogger(ZipFormats.class);

    private ZipFormats() {
    }

    /**
     * key: ZipFormat#getFormat()
     */
    private static Map<String, ZipFormat> registry = new ConcurrentHashMap<String, ZipFormat>();

    static {
        loadBuiltinZipFormats();
    }

    private static void loadBuiltinZipFormats() {
        Resource resource = Resources.loadClassPathResource("/zipformats.yml");
        InputStream stream = null;
        try {
            stream = resource.getInputStream();
            Iterable<Object> objs = new Yaml().loadAll(stream);
            Collects.forEach(objs, new Consumer<Object>() {
                @Override
                public void accept(Object o) {
                    Map<String, String> map = (Map<String, String>) o;
                    ZipFormat zipFormat = new ZipFormat();
                    if(Strings.isNotEmpty(map.get("format"))) {
                        zipFormat.setFormat(map.get("format"));
                        zipFormat.setArchive(map.get("archive"));
                        zipFormat.setDesc(map.get("desc"));
                        zipFormat.setUncompressSuffix(map.get("uncompressSuffix"));
                        zipFormat.setCompress(map.get("compress"));
                        addZipFormat(zipFormat);
                    }
                }
            });
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private static void loadCustomizedZipFormats(){

    }

    public static void addZipFormat(ZipFormat format) {
        registry.put(format.getFormat(), format);
    }

    public static ZipFormat getZipFormat(String format) {
        return registry.get(format);
    }

    public static boolean archiveEnabled(String format) {
        ZipFormat zf = getZipFormat(format);
        return zf.archiveEnabled();
    }


}
