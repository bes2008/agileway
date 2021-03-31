package com.jn.agileway.web.filter.xss;

import com.jn.langx.annotation.NotThreadSafe;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Functions;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NotThreadSafe
public class HtmlEventHandlerXssHandler extends AbstractXssHandler {

    private static final Pattern pattern = Pattern.compile("[<'\"=]");

    private static final Pattern onXxxPattern = Pattern.compile("on[a-z]+", Pattern.CASE_INSENSITIVE);

    static final Set<String> DEFAULT_ON_XXX_FUNCTION_NAMES = new LinkedHashSet<>();
    private Set<String> functionNames = new HashSet<String>();

    static {
        DEFAULT_ON_XXX_FUNCTION_NAMES.addAll(Pipeline.of(
                "onAbort",
                "onActivate",
                "onAfterPrint",
                "onAfterUpdate",
                "onBeforeActivate",
                "onBeforeCopy",
                "onBeforeCut",
                "onBeforeDeactivate",
                "onBeforeEditFocus",
                "onBeforePaste",
                "onBeforePrint",
                "onBeforeUnload",
                "onBegin",
                "onBlur",
                "onBounce",
                "onCellChange",
                "onChange",
                "onClick",
                "onContextMenu",
                "onControlSelect",
                "onCopy",
                "onCut",
                "onDataAvailable",
                "onDataSetChanged",
                "onDataSetComplete",
                "onDblClick",
                "onDeactivate",
                "onDrag",
                "onDragEnd",
                "onDragLeave",
                "onDragEnter",
                "onDragOver",
                "onDragDrop",
                "onDrop",
                "onEnd",
                "onError",
                "onErrorUpdate",
                "onFilterChange",
                "onFinish",
                "onFocus",
                "onFocusIn",
                "onFocusOut",
                "onHelp",
                "onKeyDown",
                "onKeyPress",
                "onKeyUp",
                "onLayoutComplete",
                "onLoad",
                "onLoseCapture",
                "onMediaComplete",
                "onMediaError",
                "onMouseDown",
                "onMouseEnter",
                "onMouseLeave",
                "onMouseMove",
                "onMouseOut",
                "onMouseOver",
                "onMouseUp",
                "onMouseWheel",
                "onMove",
                "onMoveEnd",
                "onMoveStart",
                "onOutOfSync",
                "onPaste",
                "onPause",
                "onProgress",
                "onPropertyChange",
                "onReadyStateChange",
                "onRepeat",
                "onReset",
                "onResize",
                "onResizeEnd",
                "onResizeStart",
                "onResume",
                "onReverse",
                "onRowsEnter",
                "onRowExit",
                "onRowDelete",
                "onRowInserted",
                "onScroll",
                "onSeek",
                "onSelect",
                "onChange",
                "onSelectStart",
                "onStart",
                "onStop",
                "onSyncRestored",
                "onSubmit",
                "onTimeError",
                "onTrackChange",
                "onUnload",
                "onURLFlip").map(Functions.toLowerCase()).asList()
        );
    }


    @Override
    protected void doInit() throws InitializationException {
        if (Objs.isEmpty(functionNames)) {
            functionNames = DEFAULT_ON_XXX_FUNCTION_NAMES;
        }
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
