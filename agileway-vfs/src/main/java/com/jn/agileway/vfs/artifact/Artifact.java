package com.jn.agileway.vfs.artifact;

public interface Artifact extends IGAV {
    String getClassifier();
    void setClassifier(String classifier);

    /**
     * 文件扩展名
     * @return
     */
    String getExtension();
    void setExtension(String extension);

    boolean isSupportSynchronized();
}
