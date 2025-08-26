package com.jn.agileway.shell.cmdline.interactive;

public class DefaultBannerSupplier implements BannerSupplier{
    private String text = "               _  _                              \n" +
            "  __ _   __ _ (_)| |  ___ __      __ __ _  _   _ \n" +
            " / _` | / _` || || | / _ \\\\ \\ /\\ / // _` || | | |\n" +
            "| (_| || (_| || || ||  __/ \\ V  V /| (_| || |_| |\n" +
            " \\__,_| \\__, ||_||_| \\___|  \\_/\\_/  \\__,_| \\__, |\n" +
            "        |___/                              |___/ \n" +
            "                                                 \n"+
            " Welcome to use agileway shell. Have fun.\n\n";
    @Override
    public String get() {
        return text;
    }
}
