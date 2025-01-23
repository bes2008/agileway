package com.jn.agileway.shell.cmdline;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

public class AnsiFontText {

    public static enum Underline{
        UNDERLINE,
        UNDERLINE_DOUBLE;
    }

    private Ansi.Color fontColor;
    private Ansi.Color backgroundColor;


    /**
     * 粗体
     */
    private boolean bold = false;
    /**
     * 斜体
     */
    private boolean italic = false;

    /**
     * 下划线
     */
    private Underline underline = null;

    /**
     * 删除线
     */
    private boolean strikethrough = false;

    /**
     * 原始文本
     */
    private String text;

    public AnsiFontText(String text){
       this.text = text;
    }

    public AnsiFontText fontColor(Ansi.Color color){
        this.fontColor= color;
        return this;
    }

    public AnsiFontText backgroundColor(Ansi.Color color){
        this.backgroundColor= color;
        return this;
    }

    public AnsiFontText bold(boolean bold){
        this.bold=bold;
        return this;
    }

    public AnsiFontText italic(boolean italic){
        this.italic=italic;
        return this;
    }

    public AnsiFontText underline(Underline underline){
        this.underline=underline;
        return this;
    }

    public AnsiFontText strikethrough(boolean strikethrough ){
        this.strikethrough=strikethrough;
        return this;
    }

    @Override
    public String toString() {
        String text = this.text == null ? "" : this.text;
        if(AnsiConsole.isInstalled()) {
            Ansi ansi = Ansi.ansi();
            if (fontColor != null) {
                ansi.fg(fontColor);
            }
            if (backgroundColor != null) {
                ansi.bg(backgroundColor);
            }
            if (bold) {
                ansi.bold();
            }
            if (italic) {
                ansi.a(Ansi.Attribute.ITALIC);
            }
            if (underline != null) {
                ansi.a(underline == Underline.UNDERLINE ? Ansi.Attribute.UNDERLINE : Ansi.Attribute.UNDERLINE_DOUBLE);
            }
            if (strikethrough) {
                ansi.a(Ansi.Attribute.STRIKETHROUGH_ON);
            }
            ansi.a(text);
            ansi.reset();
            return ansi.toString();
        }else{
            return text;
        }
    }

    public static final AnsiFontText NEWLINE = new AnsiFontText("\n");

    public static AnsiFontText ofErrorMessage(String msg){
        return new AnsiFontText(msg).fontColor(Ansi.Color.RED);
    }
    public static AnsiFontText ofBoldText(String text){
        return  new AnsiFontText(text).bold(true);
    }
}
