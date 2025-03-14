package com.jn.agileway.shell.cmdline.interactive;

import com.jn.agileway.shell.ApplicationArgs;
import com.jn.agileway.shell.cmdline.InputStreamCmdlineProvider;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.SystemPropertys;
import com.jn.langx.util.os.OS;


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
        System.out.print(this.prompt);
    }

    @Override
    protected void handleUnfinishedMultipleLineCommand(String rawLine) {
        System.out.println("cmd error");
    }
}
