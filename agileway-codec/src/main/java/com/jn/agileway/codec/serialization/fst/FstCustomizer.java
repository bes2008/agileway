package com.jn.agileway.codec.serialization.fst;

import org.nustaq.serialization.FSTConfiguration;

public interface FstCustomizer {
    String getName();

    void customize(FSTConfiguration fst);
}
