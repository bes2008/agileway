package com.jn.agileway.shell.exec;

import com.jn.langx.text.tokenizer.IterableTokenizer;

import java.util.List;

public interface CmdlineTokenizer extends IterableTokenizer<String> {
    @Override
    String peek();

    @Override
    List<String> tokenize();

    @Override
    String next();
}
