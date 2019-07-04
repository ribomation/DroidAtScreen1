/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ribomation.droidAtScreen.cmd;

import com.ribomation.droidAtScreen.Application;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Jhuan
 */
public class ChangeLanguageCommand extends Command {

    public ChangeLanguageCommand() {
        configure();
    }

    @Override
    protected void doExecute(Application app) {
        final String language = (String) JOptionPane.showInputDialog(app.getAppFrame(),
                getApplication().getLanguage().getProperty("choose_a_language"),
                getApplication().getLanguage().getProperty("language_title"),
                JOptionPane.QUESTION_MESSAGE, null,
                languages(), "English");
        if (language == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            getLog().info("setting language: " + language);
            getApplication().getSettings().setLanguage(language);
            getApplication().reloadGUI();
        });
    }

    protected String[] languages() {
        String[] languages = new String[2];

        languages[0] = "English";
        languages[1] = "Español";

        return languages;
    }

    private void configure() {
        setLabel(getString("set_language"));
        setTooltip(getString("set_language_tooltip"));
        setIcon("language");
    }
}
