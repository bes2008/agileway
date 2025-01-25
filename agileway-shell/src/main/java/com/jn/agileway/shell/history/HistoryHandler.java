package com.jn.agileway.shell.history;

import com.jn.agileway.shell.cmdline.ShellCmdlines;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Dates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.collection.queue.BoundedQueue;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.io.file.Files;
import com.jn.langx.util.logging.Loggers;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 只记录存在的命令，不存在的命令因为不会执行，所以不进行记录。
 */
public final class HistoryHandler extends AbstractInitializable {

    private File file;
    private final BoundedQueue<Record> records;

    public HistoryHandler(File file) {
        this(file,1000);
    }

    public HistoryHandler(File file, int maxRecords) {
        Preconditions.checkNotNull(file);
        this.file = file;
        this.records = new BoundedQueue<Record>(maxRecords, true, true);
    }

    @Override
    protected void doInit() throws InitializationException {
        try {
            if(!file.exists()){
                Files.makeDirs(file.getParent());
                Files.makeFile(file);
            }
            List<String> lines = Files.lines(file, Charsets.UTF_8);
            int offset = lines.size() > records.maxSize() ? (lines.size() - records.maxSize()) : 0;
            for (int i = offset; i < lines.size(); i++) {
                String line = lines.get(i);
                records.add(Record.of(line));
            }
        } catch (IOException e) {
            Loggers.getLogger(HistoryHandler.class).warn("load shell history failed: {}", e.getMessage());
        }
    }

    public void append( String[] cmdline) throws IOException {
        if (Objs.isEmpty(cmdline)) {
            return;
        }
        Record record = new Record(Dates.format(Dates.now(), Dates.yyyy_MM_dd_HH_mm_ss_SSS), ShellCmdlines.cmdlineToString(cmdline));
        records.add(record);

        Files.appendLine(file, record.toString(), Charsets.UTF_8);
    }

    public List<Record> list(int limit) {
        if (limit <= 0) {
            return Lists.immutableList();
        }
        if (limit >= records.size()) {
            return Lists.immutableList(records);
        }
        int offset = records.size() - limit;
        return Lists.immutableList(Pipeline.of(records).skip(offset).asList());
    }

    public void clean() throws IOException {
        this.records.clear();
        Files.truncate(file);
    }


}
