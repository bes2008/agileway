package com.jn.agileway.shell.command;

import java.util.List;

/**
 * CommandScanConfig类用于配置命令扫描的相关设置
 * 它包含了是否启用扫描、自定义包列表以及是否启用内置包的扫描设置
 */
public class CommandScanConfig {
    /**
     * 表示扫描功能是否启用
     * 默认情况下，扫描功能是禁用的，需要用户显式启用
     */
    private boolean enabled;

    /**
     * 存储了需要扫描的包的列表
     * 这个列表允许用户指定哪些包下的命令类需要被扫描和注册
     */
    private List<String> packages;

    /**
     * 表示是否启用内置包的扫描
     * 当启用时，系统将自动扫描预定义的包以寻找命令类
     */
    private boolean builtinPackagesEnabled;


    public boolean isBuiltinPackagesEnabled() {
        return builtinPackagesEnabled;
    }

    public void setBuiltinPackagesEnabled(boolean builtinPackagesEnabled) {
        this.builtinPackagesEnabled = builtinPackagesEnabled;
    }

    public List<String> getPackages() {
        return packages;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setPackages(List<String> packages) {
        this.packages = packages;
    }
}
