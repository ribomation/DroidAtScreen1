/*
 * Project:  droidAtScreen
 * File:     LookAndFeelCommand.java
 * Modified: 2011-10-04
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */
package com.ribomation.droidAtScreen.cmd;

import com.bulenkov.darcula.DarculaLaf;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.ribomation.droidAtScreen.Application;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.basic.BasicLookAndFeel;

/**
 * Prompts the user for a Look&Feel to set for the UI.
 *
 * @user jens
 * @date 2010-jan-18 10:35:20
 */
public class LookAndFeelCommand extends Command {

    public LookAndFeelCommand() {
        configure();
    }

    @Override
    protected void doExecute(final Application app) {
        final String lafName = (String) JOptionPane.showInputDialog(
                app.getAppFrame(),
                "Choose a Look&Feel",
                "Look&Feel",
                JOptionPane.QUESTION_MESSAGE,
                null,
                toNames(UIManager.getInstalledLookAndFeels()),
                UIManager.getLookAndFeel().getName());
        if (lafName == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            setLookAndFeel(lafName);
            SwingUtilities.updateComponentTreeUI(app.getAppFrame());
            app.getAppFrame().pack();
            app.getDevices().stream().map((frame) -> {
                SwingUtilities.updateComponentTreeUI(frame);
                return frame;
            }).forEachOrdered((frame) -> {
                frame.pack();
            });
        });
    }

    protected String[] toNames(UIManager.LookAndFeelInfo[] info) {
        Set<String> names = new TreeSet<>();
        for (UIManager.LookAndFeelInfo i : info) {
            names.add(i.getName());
        }

        names.add("Darcula");
        return names.toArray(new String[names.size()]);
    }

    protected String findClassName(String lafName) {
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if (info.getName().equals(lafName)) {
                return info.getClassName();
            }
        }
        return null;
    }

    private void configure() {
        setLabel(getString("look_and_feel"));
        setTooltip(getString("look_and_feel_tooltip"));
        setIcon("lookandfeel");
    }

    protected void setLookAndFeel(String lafName) {
        try {
            BasicLookAndFeel darcula = new DarculaLaf();
            if (lafName.equals("Darcula")) {
                UIManager.setLookAndFeel(darcula);
            } else {
                UIManager.setLookAndFeel(findClassName(lafName));
            }
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(LookAndFeelCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
