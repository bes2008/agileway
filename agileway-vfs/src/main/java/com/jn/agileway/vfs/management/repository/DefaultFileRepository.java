package com.jn.agileway.vfs.management.repository;

import com.jn.langx.AbstractNameable;
import com.jn.langx.security.crypto.digest.MessageDigests;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.logging.Loggers;

import java.net.URL;
import java.util.List;

public class DefaultFileRepository<L extends FileRepositoryLayout> extends AbstractNameable implements FileRepository<L> {
    private String id;
    private String url;
    private String protocol;
    private String basedir;
    private L layout;
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
    public void setLayout(L layout) {
        this.layout = layout;
    }

    @Override
    public L getLayout() {
        return layout;
    }

    @Override
    public String getFilePath(String relativePath) {
        return layout.getFilePath(this, relativePath);
    }

    @Override
    public String getFileDigestPath(String relativePath, String digit) {
        return layout.getFileDigestPath(this, relativePath, digit);
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
        Loggers.getLogger(getClass()).info("valid message digits algorithms: {}", Strings.join(",", digits));
        this.digits = digits;
    }

    @Override
    public List<String> getSupportedDigits() {
        // return Collects.newArrayList(JCAEStandardName.MD5.getName(), JCAEStandardName.SHA_1.getName());
        return this.digits;
    }
}
