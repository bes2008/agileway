package com.jn.agileway.shell.builtin;

import com.jn.agileway.shell.command.annotation.Command;
import com.jn.agileway.shell.command.annotation.CommandArgument;
import com.jn.agileway.shell.command.annotation.CommandComponent;
import com.jn.agileway.shell.history.HistoryHandler;
import com.jn.agileway.shell.history.Record;
import com.jn.langx.util.Strings;

import java.io.IOException;
import java.util.List;

@CommandComponent
public class History {
    private HistoryHandler handler;
    public History(HistoryHandler handler){
        this.handler = handler;
    }

    @Command(value = "history clean", desc = "clean history command records")
    public void clean() throws IOException {
        this.handler.clean();
    }

    @Command(value = "history", desc = "list history records")
    public String list(boolean displayDate,
                       @CommandArgument(value = "limit", desc = "records limit, max 1000", defaultValue = "1000")
                       int limit){
        List<Record> records = handler.list(limit);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < records.size(); i++) {
            String index = Strings.completingLength((i+1)+"",4,Strings.SP, true);
            builder.append(index).append("\t");

            Record record = records.get(i);
            if(displayDate){
                builder.append(record.getDatetime());
            }
            builder.append("  ").append(record.getCmdline());
            builder.append(Strings.CRLF);
        }

        builder.append(Strings.CRLF);
        return builder.toString();
    }

}
