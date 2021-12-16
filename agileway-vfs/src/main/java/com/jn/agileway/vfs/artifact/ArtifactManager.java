package com.jn.agileway.vfs.artifact;

import com.jn.agileway.vfs.artifact.repository.ArtifactRepository;
import com.jn.agileway.vfs.artifact.repository.ArtifactRepositoryAware;
import com.jn.agileway.vfs.management.FileDigit;
import com.jn.agileway.vfs.management.FileManager;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

import java.util.List;

/**
 * ArtifactRepository 用于提供仓库位置，仓库内文件布局方式
 * FileSystemManager 用于操作文件，例如从其他地方拿文件到仓库，从仓库读取文件等等
 */
public interface ArtifactManager extends FileManager<ArtifactRepository>, ArtifactRepositoryAware {

    /**
     * 根据 repository 获取 文件路径 path，在FileSystem中获取 path 对应的文件
     *
     * @param artifact
     * @return
     * @throws FileSystemException
     */
    FileObject getArtifactFile(Artifact artifact) throws FileSystemException;

    /**
     * @param artifact
     * @return
     */
    List<FileDigit> getDigits(Artifact artifact);

    @Override
    void setRepository(ArtifactRepository repository);

    @Override
    ArtifactRepository getRepository();
}
