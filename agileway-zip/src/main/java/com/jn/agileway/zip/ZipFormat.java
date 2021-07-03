package com.jn.agileway.zip;

public class ZipFormat {
    /**
     * 原始格式，也通常是文件的后缀
     */
    private String format;

    /**
     * format 对应的 archive format
     */
    private String archive;

    /**
     * format 对应的 compress format
     */
    private String compress;

    /**
     * 解压后的文件后缀，为null或者“” 都代表没有后缀了
     */
    private String uncompressSuffix;

    private String desc;

    public ZipFormat() {
    }

    public ZipFormat(String format, String archiveFormat, String compressFormat) {
        this.format = format;
        this.archive = archiveFormat;
        this.compress = compressFormat;
    }

    public String getUncompressSuffix() {
        return uncompressSuffix;
    }

    public void setUncompressSuffix(String uncompressSuffix) {
        this.uncompressSuffix = uncompressSuffix;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getArchive() {
        return archive;
    }

    public void setArchive(String archive) {
        this.archive = archive;
    }

    public String getCompress() {
        return compress;
    }

    public void setCompress(String compress) {
        this.compress = compress;
    }

    @Override
    public String toString() {
        return format;
    }
}
