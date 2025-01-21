package com.jn.agileway.shell.test.jansi;

import com.jn.agileway.shell.cmdline.interactive.AnsiFontText;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

public class JansiTests {
    public static void main(String[] args) {
        System.setProperty("jansi.mode", "force");
        AnsiConsole.systemInstall();
        System.out.println(AnsiConsole.isInstalled());

        System.out.print(new StringBuilder()
                .append(new AnsiFontText("hello").strikethrough(true))
                .append(" ")
                .append(new AnsiFontText("world").underline(AnsiFontText.Underline.UNDERLINE_DOUBLE))
                .append(AnsiFontText.NEWLINE)
                .toString()
        );
        System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("Hello2").fg(Ansi.Color.GREEN).a(" World2").reset());
    }
}
