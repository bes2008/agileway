package com.jn.agileway.shell.test.cmdline;

import com.jn.agileway.shell.cmdline.ShellCmdlines;
import org.junit.jupiter.api.Test;


public class ShellLineTokenizerTests {
    @Test
    public void test(){
        showAsTokens("echo \"Today's date is $(date)\" hello world");
        showAsTokens("openssl req -new -key private.key -out req_csr.pem");
        showAsTokens("echo '\"This is a backslash: \\ and this is a double quote: \"'");
        showAsTokens("mv \"/path/to/my file\" /path/to/newlocation");
        showAsTokens("echo \"Today's date is $(date)\"");
    }

    private void showAsTokens(String cmdline){
        System.out.println(ShellCmdlines.cmdlineToArgs(cmdline));
    }
}
