package com.jn.agileway.web.filter.xss;

import com.jn.langx.annotation.NotThreadSafe;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NotThreadSafe
public class HtmlEventHandlerXssHandler extends AbstractXssHandler {

    private static final Pattern pattern = Pattern.compile("[<'\"=]");

    private static final Pattern onXxxPattern = Pattern.compile("on[a-z]+", Pattern.CASE_INSENSITIVE);

    private static final Set<String> DEFAULT_ON_XXX_FUNCTION_NAMES = new HashSet<String>();
    private Set<String> excludedFunctionNames = new HashSet<String>();
    private Set<String> functionNames = new HashSet<String>();
    private boolean inited = false;

    static {
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onerror");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onclick");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onload");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onAbort");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onActivate");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onAfterPrint");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onAfterUpdate");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onBeforeActivate");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onBeforeCopy");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onBeforeCut");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onBeforeDeactivate");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onBeforeEditFocus");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onBeforePaste");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onBeforePrint");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onBeforeUnload");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onBegin");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onBlur");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onBounce");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onCellChange");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onChange");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onClick");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onContextMenu");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onControlSelect");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onCopy");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onCut");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onDataAvailable");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onDataSetChanged");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onDataSetComplete");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onDblClick");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onDeactivate");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onDrag");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onDragEnd");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onDragLeave");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onDragEnter");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onDragOver");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onDragDrop");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onDrop");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onEnd");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onError");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onErrorUpdate");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onFilterChange");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onFinish");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onFocus");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onFocusIn");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onFocusOut");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onHelp");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onKeyDown");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onKeyPress");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onKeyUp");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onLayoutComplete");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onLoad");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onLoseCapture");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onMediaComplete");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onMediaError");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onMouseDown");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onMouseEnter");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onMouseLeave");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onMouseMove");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onMouseOut");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onMouseOver");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onMouseUp");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onMouseWheel");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onMove");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onMoveEnd");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onMoveStart");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onOutOfSync");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onPaste");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onPause");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onProgress");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onPropertyChange");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onReadyStateChange");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onRepeat");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onReset");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onResize");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onResizeEnd");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onResizeStart");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onResume");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onReverse");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onRowsEnter");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onRowExit");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onRowDelete");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onRowInserted");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onScroll");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onSeek");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onSelect");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onChange");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onSelectStart");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onStart");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onStop");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onSyncRestored");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onSubmit");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onTimeError");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onTrackChange");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onUnload");
        addOnXxxWithLowerCase(DEFAULT_ON_XXX_FUNCTION_NAMES, "onURLFlip");
    }

    public void setExcludedFunctionNames(Collection<String> functionNames) {
        Collects.forEach(functionNames, new Consumer<String>() {
            @Override
            public void accept(String functionName) {
                addOnXxxWithLowerCase(excludedFunctionNames, functionName);
            }
        });

    }

    private static void addOnXxxWithLowerCase(Set<String> set, String name) {
        if (Objs.isNotEmpty(name)) {
            set.add(name.toLowerCase());
        }
    }

    @Override
    protected void doInit() throws InitializationException {
        Collects.forEach(DEFAULT_ON_XXX_FUNCTION_NAMES,
                new Predicate<String>() {
                    @Override
                    public boolean test(String functionName) {
                        return !excludedFunctionNames.contains(functionName);
                    }
                },
                new Consumer<String>() {
                    @Override
                    public void accept(String functionName) {
                        functionNames.add(functionName);
                    }
                });
    }


    protected boolean isAttack(String value) {
        init();
        if (Objs.isEmpty(value)) {
            return false;
        }
        if (value.length() < 6) {
            return false;
        }
        boolean hasBadCode = pattern.matcher(value).find();
        if (!hasBadCode) {
            return false;
        }
        Matcher m = onXxxPattern.matcher(value);
        while (m.find()) {
            String name = m.group();
            name = name.toLowerCase();
            if (functionNames.contains(name)) {
                return true;
            }
        }
        return false;
    }

}
