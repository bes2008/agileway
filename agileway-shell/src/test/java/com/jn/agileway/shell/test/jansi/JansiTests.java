package com.jn.agileway.shell.test.jansi;

import com.jn.agileway.shell.util.AnsiText;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

public class JansiTests {
    public static void main(String[] args) {
        System.setProperty("jansi.mode", "force");
        AnsiConsole.systemInstall();
        System.out.println(AnsiConsole.isInstalled());

        System.out.print(new StringBuilder()
                .append(new AnsiText("hello").strikethrough(true))
                .append(" ")
                .append(new AnsiText("world").underline(AnsiText.Underline.UNDERLINE_DOUBLE))
                .append(AnsiText.NEWLINE)
                .toString()
        );
        System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("Hello2").fg(Ansi.Color.GREEN).a(" World2").reset());
    }
}
