package com.jn.agileway.shell.test.terminal;
/*

import com.jn.agileway.shell.terminal.TerminalController;
import com.jn.agileway.shell.terminal.Terminals;
import com.jn.agileway.shell.terminal.WindowsTerminalController;

import java.io.IOException;
import java.io.InputStreamReader;

public class InputTests {

    public static void main(String[] args) throws IOException {

        boolean isNativeTerminal = Terminals.isNativeTerminal();
        System.out.println(isNativeTerminal);

        TerminalController terminalController = new WindowsTerminalController();
        terminalController.setTitle("test");

        boolean success = terminalController.enableLineInputMode(false);
        if(!success){
            System.out.println("disable input buffer error: "+terminalController.getLastErrorMessage());
        }
        System.out.println(success);
        InputStreamReader input = new InputStreamReader(terminalController.getInputStream());
        System.out.println("type: ");
        int ch = input.read();
        while (ch!=-1){
            ch = input.read();
            System.out.println(ch);
        }

    }

}


 */