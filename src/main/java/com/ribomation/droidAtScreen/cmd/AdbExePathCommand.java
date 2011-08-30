package com.ribomation.droidAtScreen.cmd;

import com.ribomation.droidAtScreen.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * DESCRIPTION
 *
 * @user jens
 * @date 2010-jan-18 10:35:20
 */
public class AdbExePathCommand extends Command {
    public AdbExePathCommand() {
        setLabel("ADB Executable Path");
        setTooltip("Sets the path to the Android Device Debugger (ADB) executable");
//        setIcon("Shutdown");
//        setMnemonic('Q');
    }

    @Override
    protected void doExecute(Application app) {
        JOptionPane.showMessageDialog(app.getAppFrame(),
                createPane(),
                "ADB Executable",
                JOptionPane.QUESTION_MESSAGE);
    }

    @Override
    public JPanel createPane() {
        JPanel  pane = new JPanel(new BorderLayout(0,5));
        pane.setBorder( BorderFactory.createTitledBorder("Path to ADB Executable") );

        pane.add(createInfoPane(), BorderLayout.CENTER);
        pane.add(createPathPane(), BorderLayout.SOUTH);

        return pane;
    }

    private JPanel createPathPane() {
        final JTextField      path = new JTextField( getPreferenceValue() );
        JButton         open = new JButton("...");
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createFileDialog(path);
            }
        });

        JPanel  pathPane = new JPanel(new BorderLayout(5,0));
        pathPane.add(path, BorderLayout.CENTER);
        pathPane.add(open, BorderLayout.EAST);
        return pathPane;
    }

    private void createFileDialog(JTextField path) {
        File f = null;
        if (! isNotDefined()) {
            f = new File(getPreferenceValue());
        }
        JFileChooser chooser = new JFileChooser(f != null ? f.getParentFile() : null);

        int rc = chooser.showOpenDialog( getApplication().getAppFrame() );
        if (rc == JFileChooser.APPROVE_OPTION) {
            final File    file = chooser.getSelectedFile();

            if (file.canRead() && file.canExecute()) {
                path.setText( file.getAbsolutePath() );
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        setPreferenceValue( file.getAbsolutePath() );
                        getApplication().setAdbExecutablePath(file);
                    }
                });
            } else {
                JOptionPane.showMessageDialog(getApplication().getAppFrame(),
                        "Cannot read/execute the file: "+file.getAbsolutePath(),
                        "Not an executable",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JTextArea createInfoPane() {
        JTextArea infoPane = new JTextArea();
        infoPane.setEditable(false);
//        infoPane.setEnabled(false);
        infoPane.setColumns(50);
        infoPane.setRows(4);
        infoPane.setLineWrap(true);
        infoPane.setText("In order to establish a connection to a device, this application uses the " +
                "Android Debug Bridge (ADB). If ADB is not running when this program launches, it will start ADB. " +
                "For this to work, it needs the full path to the ADB executable. " +
                "Please, provide it below.");
        return infoPane;
    }

    protected String getPreferencesKey() {
        return "adb-executable-path";
    }

    public void setPreferenceValue(String value) {
        getApplication().getPreferences().put(getPreferencesKey(), value);
        getApplication().savePreferences();
    }

    protected String getPreferenceValue() {
        return getApplication().getPreferences().get(getPreferencesKey(), "");
    }

    public boolean isNotDefined() {
        return isEmpty( getPreferenceValue() );
    }

    public File getFile() {
        if (isNotDefined()) return null;
        return new File( getPreferenceValue() );
    }

    private boolean isEmpty(String s) {
        return (s == null) || (s.trim().length() == 0);
    }
}
