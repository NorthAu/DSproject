package com.example.textbookmgmt.ui;

import com.example.textbookmgmt.entity.TextbookType;
import com.example.textbookmgmt.service.TextbookTypeService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TextbookTypePanel extends JPanel {
    private final TextbookTypeService service;
    private final JTextField nameField = new JTextField(20);
    private final JTextArea descriptionField = new JTextArea(3, 20);
    private final TypeTableModel tableModel = new TypeTableModel();
    private final JTable table = new JTable(tableModel);

    public TextbookTypePanel(TextbookTypeService service) {
        super(new BorderLayout(10, 10));
        this.service = service;
        setBorder(new EmptyBorder(6, 6, 6, 6));

        add(buildForm(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
        reloadAsync();
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("教材类型"));
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
        panel.add(new JLabel("描述:"), gbc);
        gbc.gridx = 1;
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        panel.add(new JScrollPane(descriptionField), gbc);
        return panel;
    }

    private JScrollPane buildTable() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getSelectionModel().addListSelectionListener(e -> fillForm());
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("类型列表"));
        return scrollPane;
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        JButton save = new JButton("保存/更新");
        save.addActionListener(e -> onSave());
        JButton delete = new JButton("删除");
        delete.addActionListener(e -> onDelete());
        JButton refresh = new JButton("刷新");
        refresh.addActionListener(e -> reloadAsync());
        JButton clear = new JButton("清空表单");
        clear.addActionListener(e -> clear());
        panel.add(save);
        panel.add(delete);
        panel.add(refresh);
        panel.add(clear);
        return panel;
    }

    private void onSave() {
        try {
            TextbookType type = new TextbookType();
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                type.setId((Long) tableModel.getValueAt(selected, 0));
            }
            type.setName(nameField.getText());
            type.setDescription(descriptionField.getText());
            service.save(type);
            reloadAsync();
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
        service.delete(id);
        reloadAsync();
        clear();
    }

    public void reloadAsync() {
        SwingWorker<List<TextbookType>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<TextbookType> doInBackground() {
                return service.list();
            }

            @Override
            protected void done() {
                try {
                    tableModel.setData(get());
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    JOptionPane.showMessageDialog(TextbookTypePanel.this, "加载类型失败: " + ex.getCause().getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void clear() {
        nameField.setText("");
        descriptionField.setText("");
        table.clearSelection();
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row >= 0 && row < tableModel.getRowCount()) {
            nameField.setText((String) tableModel.getValueAt(row, 1));
            descriptionField.setText((String) tableModel.getValueAt(row, 2));
        }
    }

    static class TypeTableModel extends AbstractTableModel {
        private final String[] columns = {"ID", "名称", "描述"};
        private List<TextbookType> data = new ArrayList<>();

        public void setData(List<TextbookType> data) {
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
            TextbookType type = data.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> type.getId();
                case 1 -> type.getName();
                case 2 -> type.getDescription();
                default -> null;
            };
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? Long.class : String.class;
        }
    }
}
