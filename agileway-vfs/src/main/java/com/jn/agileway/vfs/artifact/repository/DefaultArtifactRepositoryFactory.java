package com.jn.agileway.vfs.artifact.repository;


import com.jn.langx.registry.GenericRegistry;
import com.jn.langx.registry.Registry;
import com.jn.langx.util.Preconditions;

public class DefaultArtifactRepositoryFactory extends GenericRegistry<ArtifactRepository> implements ArtifactRepositoryFactory {
    private Registry<String, ArtifactRepositoryLayout> registry;

    @Override
    public void setArtifactRepositoryLayoutRegistry(Registry<String, ArtifactRepositoryLayout> registry) {
        this.registry = registry;
    }

    @Override
    public Registry<String, ArtifactRepositoryLayout> getRegistry() {
        return registry;
    }

    @Override
    public ArtifactRepository get(ArtifactRepositoryProperties props) {
        DefaultArtifactRepository repository = new DefaultArtifactRepository();
        repository.setBasedir(props.getBasedir());
        repository.setId(props.getName());
        repository.setName(props.getName());
        repository.setUrl(props.getUrl());
        ArtifactRepositoryLayout layout = registry.get(props.getName());
        Preconditions.checkNotNull(layout);
        repository.setLayout(layout);
        register(repository);
        return repository;
    }
}
