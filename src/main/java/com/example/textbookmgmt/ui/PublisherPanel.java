package com.example.textbookmgmt.ui;

import com.example.textbookmgmt.entity.Publisher;
import com.example.textbookmgmt.service.PublisherService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PublisherPanel extends JPanel {
    private final PublisherService publisherService;
    private final JTextField nameField = new JTextField(20);
    private final JTextField contactField = new JTextField(20);
    private final PublisherTableModel tableModel = new PublisherTableModel();
    private final JTable table = new JTable(tableModel);

    public PublisherPanel(PublisherService publisherService) {
        super(new BorderLayout(10, 10));
        this.publisherService = publisherService;
        setBorder(new EmptyBorder(6, 6, 6, 6));

        add(buildForm(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
        reload();
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("出版社信息"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("名称:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("联系方式:"), gbc);
        gbc.gridx = 1;
        panel.add(contactField, gbc);
        return panel;
    }

    private JScrollPane buildTable() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getSelectionModel().addListSelectionListener(e -> fillForm());
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("出版社列表"));
        return scrollPane;
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        JButton save = new JButton("保存/更新");
        save.addActionListener(e -> onSave());
        JButton delete = new JButton("删除");
        delete.addActionListener(e -> onDelete());
        JButton clear = new JButton("清空表单");
        clear.addActionListener(e -> clear());
        panel.add(save);
        panel.add(delete);
        panel.add(clear);
        return panel;
    }

    private void onSave() {
        try {
            Publisher publisher = new Publisher();
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                publisher.setId((Long) tableModel.getValueAt(selected, 0));
            }
            publisher.setName(nameField.getText());
            publisher.setContact(contactField.getText());
            publisherService.save(publisher);
            reload();
            clear();
            JOptionPane.showMessageDialog(this, "保存成功", "提示", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "校验失败", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的记录", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        long id = (Long) tableModel.getValueAt(row, 0);
        publisherService.delete(id);
        reload();
        clear();
    }

    private void reload() {
        tableModel.setData(publisherService.list());
    }

    private void clear() {
        nameField.setText("");
        contactField.setText("");
        table.clearSelection();
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row >= 0 && row < tableModel.getRowCount()) {
            nameField.setText((String) tableModel.getValueAt(row, 1));
            contactField.setText((String) tableModel.getValueAt(row, 2));
        }
    }

    static class PublisherTableModel extends AbstractTableModel {
        private final String[] columns = {"ID", "名称", "联系方式"};
        private List<Publisher> data = new ArrayList<>();

        public void setData(List<Publisher> data) {
            this.data = data;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Publisher publisher = data.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> publisher.getId();
                case 1 -> publisher.getName();
                case 2 -> publisher.getContact();
                default -> null;
            };
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? Long.class : String.class;
        }

        public int getRowCountSafe() {
            return data.size();
        }
    }
}
