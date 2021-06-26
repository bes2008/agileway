package com.jn.agileway.vfs.artifact.repository;

import com.jn.agileway.vfs.artifact.Artifact;
import com.jn.langx.Named;

import java.util.List;

public interface ArtifactRepository extends Named {
    String getId();
    void setId(String id);

    String getUrl();
    void setUrl(String url);

    void setBasedir(String basedir);
    String getBasedir();
    String getProtocol();

    void setLayout(ArtifactRepositoryLayout layout);
    ArtifactRepositoryLayout getLayout();

    String getPath(Artifact artifact);
    String getDigitPath(Artifact artifact, String digit);

    boolean isEnabled();

    boolean isDigitSupports();
    List<String> getSupportedDigits();
    void setSupportedDigits(List<String> digits);
}
