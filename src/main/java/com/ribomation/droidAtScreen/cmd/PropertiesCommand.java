/*
 * Project:  droidAtScreen
 * File:     PropertiesCommand.java
 * Modified: 2012-04-11
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */
package com.ribomation.droidAtScreen.cmd;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import com.ribomation.droidAtScreen.Application;
import com.ribomation.droidAtScreen.gui.DeviceFrame;

/**
 * Shows a table with all device properties.
 *
 * @user Jens
 * @date 2012-04-11, 09:46
 */
public class PropertiesCommand extends CommandWithTarget<DeviceFrame> {

    public PropertiesCommand(DeviceFrame target) {
        super(target);
        configure(target);
    }

    @Override
    protected void updateButton(DeviceFrame target) {
        setIcon("list");
        setTooltip(getString("properties_tooltip"));
    }

    @Override
    protected void doExecute(Application app, DeviceFrame target) {
        Map<String, String> properties = target.getDevice().getProperties();
        final String toolTipText = getString("device_properties_tooltip");

        PropertiesModel model = new PropertiesModel(properties);
        JTable tbl = new JTable(model) {
            @Override
            public String getToolTipText(MouseEvent event) {
                return toolTipText;
            }
        };
        tbl.getTableHeader().setToolTipText(toolTipText);
        tbl.setRowSelectionAllowed(true);
        tbl.getSelectionModel().addListSelectionListener(model);
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbl.setShowHorizontalLines(true);
        tbl.setFillsViewportHeight(true);
        tbl.setPreferredScrollableViewportSize(new Dimension(400, 200));

        JScrollPane pane = new JScrollPane(tbl);
        JOptionPane.showMessageDialog(app.getAppFrame(), pane,
                getString("device_properties"), JOptionPane.PLAIN_MESSAGE);
    }

    private void configure(DeviceFrame deviceFrame) {
        updateButton(deviceFrame);
    }

    class PropertiesModel extends AbstractTableModel implements ListSelectionListener {

        private final List<String> names = new ArrayList<>();
        private final List<String> values = new ArrayList<>();

        PropertiesModel(Map<String, String> properties) {
            new TreeSet<>(properties.keySet()).stream().map((name) -> {
                names.add(name);
                return name;
            }).forEachOrdered((name) -> {
                values.add(properties.get(name));
            });
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            if (lsm.isSelectionEmpty()) {
                return;
            }
            int row = lsm.getMinSelectionIndex();

            JTextArea txt = new JTextArea((String) getValueAt(row, 1));
            txt.setRows(6);
            txt.setColumns(36);
            txt.setLineWrap(true);
            txt.setWrapStyleWord(true);
            txt.setEditable(false);

            JOptionPane.showMessageDialog(getApplication().getAppFrame(), txt, (String) getValueAt(row, 0), JOptionPane.PLAIN_MESSAGE);
        }

        @Override
        public Object getValueAt(int row, int col) {
            if (col == 0) {
                return names.get(row);
            }
            if (col == 1) {
                return values.get(row);
            }
            return null;
        }

        @Override
        public String getColumnName(int col) {
            if (col == 0) {
                return getString("name");
            }
            if (col == 1) {
                return getString("value");
            }
            return "";
        }

        @Override
        public int getRowCount() {
            return names.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        @Override
        public Class<?> getColumnClass(int col) {
            if (col == 0) {
                return String.class;
            }
            if (col == 1) {
                return String.class;
            }
            return Object.class;
        }
    }
}
