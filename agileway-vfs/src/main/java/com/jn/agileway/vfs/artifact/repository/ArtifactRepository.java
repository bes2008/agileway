package com.jn.agileway.vfs.artifact.repository;

import com.jn.agileway.vfs.artifact.Artifact;
import com.jn.agileway.vfs.management.repository.FileRepository;

/**
 *
 * 代表仓库元数据信息，并可以根据它来计算指定的 artifact的path
 *
 * ${url}/${basedir}/${artifact}
 *
 */
public interface ArtifactRepository extends FileRepository<ArtifactRepositoryLayout> {

    void setLayout(ArtifactRepositoryLayout layout);

    ArtifactRepositoryLayout getLayout();


     String getPath(Artifact artifact);

    String getDigitPath(Artifact artifact, String digest) ;
}
