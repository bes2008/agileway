package com.jn.agileway.codec.serialization.fse;

import com.jfireframework.fse.Fse;

public interface FseCustomizer {
    String getName();

    void customize(Fse fse);
}
