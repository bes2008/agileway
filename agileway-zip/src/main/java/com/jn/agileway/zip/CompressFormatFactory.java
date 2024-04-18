package com.jn.agileway.zip;

import com.jn.langx.util.function.Supplier0;

import java.util.List;

public interface CompressFormatFactory extends Supplier0<List<CompressFormat>> {
    @Override
    List<CompressFormat> get();
}
