package com.jn.agileway.web.filter.waf.sqlinjection;

import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.struct.Holder;

import java.util.List;

public class SqlCharRemoveHandler extends SqlWAFHandler{
    private final List<Character> SINGLE_CHARS = Collects.asList(
            '|', '&', ';', '$', '%', '@', '\'', '"', '<', '>', '(', ')', '+', '\t', '\r', '\f', ',', '\\'
    );

    protected List<Character> removedChars = null;

    public void setRemovedChars(List<Character> removed_chars) {
        this.removedChars = removed_chars;
    }

    public List<Character> getRemovedChars() {
        return Objs.useValueIfEmpty(removedChars, SINGLE_CHARS);
    }

    @Override
    public String apply(String value) {
        final Holder<String> stringHolder = new Holder<String>(value);
        Collects.forEach(getRemovedChars(), new Consumer<Character>() {
            @Override
            public void accept(Character character) {
                String v = stringHolder.get();
                v = Strings.remove(v, character);
                stringHolder.set(v);
            }
        }, new Predicate<Character>() {
            @Override
            public boolean test(Character character) {
                return stringHolder.isEmpty();
            }
        });
        return stringHolder.get();
    }

    @Override
    public String getAttackName() {
        return "SQL-Inject";
    }

    @Override
    protected boolean isAttack(String value) {
        return false;
    }
}
