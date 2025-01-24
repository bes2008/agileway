package com.jn.agileway.shell.cmdline.interactive;

public class DefaultBannerSupplier implements BannerSupplier{
    String str = "             _ _                          \n" +
            "  __ _  __ _(_) | _____      ____ _ _   _ \n" +
            " / _` |/ _` | | |/ _ \\ \\ /\\ / / _` | | | |\n" +
            "| (_| | (_| | | |  __/\\ V  V / (_| | |_| |\n" +
            " \\__,_|\\__, |_|_|\\___| \\_/\\_/ \\__,_|\\__, |\n" +
            "       |___/                        |___/ \n" +
            "Welcome to use agileway-shell, have fun. ";
    @Override
    public String get() {
        return str;
    }
}
