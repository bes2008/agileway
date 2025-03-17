package com.jn.agileway.shell.cmdline.interactive;

import com.jn.agileway.shell.ApplicationArgs;
import com.jn.agileway.shell.cmdline.BufferedCmdlineReader;
import com.jn.agileway.shell.cmdline.InputStreamCmdlineProvider;
import com.jn.agileway.shell.cmdline.interactive.jline2.Jline2CmdlineReader;
import com.jn.agileway.shell.util.AnsiTerminals;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.SystemPropertys;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.os.OS;
import org.fusesource.jansi.AnsiConsole;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class InteractiveModeCmdlineProvider extends InputStreamCmdlineProvider {
    private String prompt;
    private String banner;
    private boolean bannerUsed = false;

    public InteractiveModeCmdlineProvider(ApplicationArgs appArgs, PromptSupplier promptSupplier, BannerSupplier bannerSupplier) {
        super(appArgs);
        this.prompt = Strings.trimToEmpty(promptSupplier.get()) + getGuideChar() + " ";
        String b = bannerSupplier.get();
        if(Strings.isBlank(b)){
            b = Strings.EMPTY;
        }
        this.banner = b;

        init();
        this.reader.setPrompt(this.prompt);
    }

    @Override
    protected void initCmdlineReader() {
        if(AnsiTerminals.isInstalled()){
            // 尝试使用 jline3 reader
            if(this.reader==null){
                //
            }
            // 尝试使用 jline2 reader
            if(this.reader==null) {
                try {
                    this.reader = new Jline2CmdlineReader();
                } catch (Throwable ex) {
                    Logger logger = Loggers.getLogger(InteractiveModeCmdlineProvider.class);
                    logger.info("create jline-2 reader failed: {}", ex.getMessage());
                }
            }
        }
        if(reader == null) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in, Charsets.getDefault()));
            this.reader = new BufferedCmdlineReader(bufferedReader);
        }

    }

    private char getGuideChar() {
        String osUserName = SystemPropertys.getUserName();
        boolean isSupperUser = false;
        if (OS.isFamilyLinux() || OS.isFamilyMac() || OS.isFamilyAIX() || OS.isFamilyHP_UX()) {
            if (Objs.equals("root", osUserName)) {
                isSupperUser = true;
            }
        } else if (OS.isFamilyWindows()) {
            if (Strings.equalsIgnoreCase("administrator", osUserName)) {
                isSupperUser = true;
            }
        }
        return isSupperUser ? '#' : '>';
    }


    @Override
    protected void beforeRead() {
        if (!this.bannerUsed && Strings.isNotEmpty(banner)) {
            this.bannerUsed = true;
            System.out.println(this.banner);
        }
    }

    @Override
    protected void handleUnfinishedMultipleLineCommand(String rawLine) {
        System.out.println("cmd error");
    }
}
