package com.jn.agileway.web.security.xss;

import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class HtmlTagXssHandler extends AbstractRegexpXssHandler {

    static final Set<String> DEFAULT_TAGS = Collects.unmodifiableSet(
            "style", "script","div","span","input",
            "a","abbr","acronym","address","applet","area","article","aside","audio",
            "b","base","basefont","bdi","bdo","bgsound","big","blink","blockquote","br","body","button",
            "canvas","caption","center","cite","code","col","colgroup","content",
            "data","datalist","dd","del","details","dfn","dialog","dir","div","dl","dt",
            "em","embed",
            "fieldset","figcaption","figure","font","footer","form","frame","frameset",
            "hd","hr","head","header","h1","hgroup","hr","html",
            "i","iframe","image","img","ins","isindex",
            "kbd","keygen",
            "label","legend",
            "li","link","listing","main","map","mark","marquee","menu","menuitem","meta","meta","meter","multicol",
            "nav","nextid","nobr","noembed","noframes","noscript",
            "object","ol","optgroup","output",
            "p","param","picture","plaintext","portal","pre","progress",
            "q",
            "rb","rt","rtc","ruby",
            "s","samp","section","select","shadow","slot","small","source","spacer","span","style","sub","strike","strong","summary","sup",
            "thead","tbody","tfoot","table","tr","td","template","textarea","time","title","tt","track",
            "u","ul",
            "var","video","wbr","xmp"
    );

    private List<Pattern> includePatterns;

    @Override
    protected List<Pattern> getIncludedPatterns() {
        return includePatterns;
    }

    public void setIncludeTags(Collection<String> tags) {
        this.includePatterns = toPatterns(tags);
    }

    private static List<Pattern> toPatterns(Collection<String> tags) {
        return Pipeline.of(tags).clearNulls().distinct().map(new Function<String, Pattern>() {
            @Override
            public Pattern apply(String tag) {
                return Pattern.compile(".*<" + tag + ".*(/)?>((.*?)</" + tag + ">.*)?", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            }
        }).asList();
    }

    @Override
    protected void doInit() throws InitializationException {
        if (Objs.isEmpty(includePatterns)) {
            includePatterns = toPatterns(DEFAULT_TAGS);
        }
    }
}
