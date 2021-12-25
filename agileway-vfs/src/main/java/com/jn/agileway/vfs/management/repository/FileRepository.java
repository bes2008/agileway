package com.jn.agileway.vfs.management.repository;

import com.jn.langx.Nameable;

import java.util.List;

/**
 * 代表仓库元数据信息，并可以根据它来计算指定的 路径的全路径
 * <p>
 * ${url}/${basedir}/${relativePath}
 */
public interface FileRepository<L extends FileRepositoryLayout> extends Nameable {
    String getId();

    void setId(String id);

    /**
     * @return root path
     */
    String getUrl();

    void setUrl(String url);

    /**
     * @param basedir the base dir after root
     */
    void setBasedir(String basedir);

    String getBasedir();

    String getProtocol();

    void setLayout(L layout);

    L getLayout();

    /**
     * 获取 ${url}/${basedir}/${relativePath}
     */
    String getFilePath(String relativePath);

    /**
     * 获取 ${url}/${basedir}/${relativePath}.${digit}
     */
    String getFileDigestPath(String relativePath, String digit);

    boolean isEnabled();

    boolean isDigitSupports();

    List<String> getSupportedDigits();

    void setSupportedDigits(List<String> digits);
}
