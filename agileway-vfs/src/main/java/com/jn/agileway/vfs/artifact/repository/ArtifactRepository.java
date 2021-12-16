package com.jn.agileway.vfs.artifact.repository;

import com.jn.agileway.vfs.artifact.Artifact;
import com.jn.langx.Nameable;

import java.util.List;

/**
 *
 * 代表仓库元数据信息，并可以根据它来计算指定的 artifact的path
 *
 * ${url}/${basedir}/${artifact}
 *
 */
public interface ArtifactRepository extends Nameable {
    String getId();

    void setId(String id);

    /**
     * root path
     *
     * @return
     */
    String getUrl();

    void setUrl(String url);

    /**
     * @param basedir the base dir after root
     */
    void setBasedir(String basedir);

    String getBasedir();

    String getProtocol();

    void setLayout(ArtifactRepositoryLayout layout);

    ArtifactRepositoryLayout getLayout();

    String getPath(Artifact artifact);

    String getDigitPath(Artifact artifact, String digit);

    /**
     * 获取 ${url}/${basedir}/${relativePath}
     */
    String getPath(String relativePath);

    boolean isEnabled();

    boolean isDigitSupports();

    List<String> getSupportedDigits();

    void setSupportedDigits(List<String> digits);
}
