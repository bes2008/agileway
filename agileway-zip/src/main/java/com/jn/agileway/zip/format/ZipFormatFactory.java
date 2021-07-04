package com.jn.agileway.zip.format;

import com.jn.langx.util.function.Supplier0;

import java.util.List;

public interface ZipFormatFactory extends Supplier0<List<ZipFormat>> {
    @Override
    List<ZipFormat> get();
}
