package com.jn.agileway.vfs.artifact.repository;

import com.jn.agileway.vfs.artifact.Artifact;
import com.jn.langx.security.MessageDigests;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;

public class DefaultArtifactRepository implements ArtifactRepository {
    private static final Logger logger = LoggerFactory.getLogger(DefaultArtifactRepository.class);
    private String id;
    private String name;
    private String url;
    private String protocol;
    private String basedir;
    private ArtifactRepositoryLayout layout;
    private boolean isEnabled = false;
    private List<String> digits;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public void setUrl(String url) {
        if (Strings.isNotEmpty(url)) {
            try {
                URL u = new URL(url);
                this.protocol = u.getProtocol();
                this.url = url;
            } catch (Throwable ex) {
                throw Throwables.wrapAsRuntimeException(ex);
            }
        }
    }

    @Override
    public String getBasedir() {
        return this.basedir;
    }

    @Override
    public void setBasedir(String basedir) {
        this.basedir = basedir;
    }

    @Override
    public String getProtocol() {
        return this.protocol;
    }

    @Override
    public void setLayout(ArtifactRepositoryLayout layout) {
        this.layout = layout;
    }

    @Override
    public ArtifactRepositoryLayout getLayout() {
        return layout;
    }

    @Override
    public String getPath(Artifact artifact) {
        return layout.getPath(this, artifact);
    }

    @Override
    public String getDigitPath(Artifact artifact, String digit) {
        Preconditions.checkTrue(Collects.contains(getSupportedDigits(), digit));
        return layout.getDigitPath(this, artifact, digit);
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public boolean isDigitSupports() {
        return Objs.isNotEmpty(this.digits);
    }

    @Override
    public void setSupportedDigits(List<String> digits) {
        digits = Pipeline.of(digits)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String algorithm) {
                        return MessageDigests.isKnownMessageDigitAlgorithm(algorithm);
                    }
                }).asList();
        logger.info("valid message digits algorithms: {}", Strings.join(",", digits));
        this.digits = digits;
    }

    @Override
    public List<String> getSupportedDigits() {
        // return Collects.newArrayList(JCAEStandardName.MD5.getName(), JCAEStandardName.SHA_1.getName());
        return this.digits;
    }

}
