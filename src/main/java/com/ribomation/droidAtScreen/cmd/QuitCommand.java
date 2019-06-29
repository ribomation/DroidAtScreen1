/*
 * Project:  droidAtScreen
 * File:     QuitCommand.java
 * Modified: 2011-10-04
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */
package com.ribomation.droidAtScreen.cmd;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.ribomation.droidAtScreen.Application;

/**
 * Quits the application, but asking first.
 *
 * @user jens
 * @date 2010-jan-18 10:35:20
 */
public class QuitCommand extends Command {

    public QuitCommand() {
        configure();
    }

    @Override
    protected void doExecute(Application app) {
        if (!app.getSettings().isAskBeforeQuit() || askUser(app)) {
            doQuit(app);
        }
    }

    private void doQuit(Application app) {
        JFrame f = app.getAppFrame();
        if (f != null) {
            f.dispose();
        }
        System.exit(0);
    }

    private boolean askUser(Application app) {
        int rc = JOptionPane.showConfirmDialog(app.getAppFrame(),
                app.getLanguage().getProperty("quit_question"), 
                app.getLanguage().getProperty("quit_question_title"), 
                JOptionPane.OK_CANCEL_OPTION);
        return rc == JOptionPane.OK_OPTION;
    }

    private void configure() {
        setLabel("quit");
        setTooltip("quit_tooltip");
        setIcon("exit");
        setMnemonic('Q');
    }
}
