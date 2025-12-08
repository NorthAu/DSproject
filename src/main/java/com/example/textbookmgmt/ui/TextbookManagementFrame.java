package com.example.textbookmgmt.ui;

import com.example.textbookmgmt.entity.Textbook;
import com.example.textbookmgmt.service.TextbookService;
import com.example.textbookmgmt.ui.model.TextbookTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TextbookManagementFrame extends JFrame {
    private final TextbookService textbookService;
    private final TextbookTableModel tableModel = new TextbookTableModel();
    private final JTable table = new JTable(tableModel);
    private final TextbookFormPanel formPanel = new TextbookFormPanel();

    public TextbookManagementFrame(TextbookService textbookService) {
        super("高校教材管理系统");
        this.textbookService = textbookService;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(950, 620));

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> onTableSelection());

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        reloadTable();
    }

    private JPanel createButtonPanel() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addButton = new JButton("新增");
        addButton.addActionListener(e -> onAdd());
        JButton updateButton = new JButton("修改");
        updateButton.addActionListener(e -> onUpdate());
        JButton deleteButton = new JButton("删除");
        deleteButton.addActionListener(e -> onDelete());
        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> reloadTable());

        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(refreshButton);
        return buttons;
    }

    private void onAdd() {
        try {
            Textbook textbook = formPanel.toTextbook(null);
            textbookService.create(textbook);
            reloadTable();
            formPanel.reset();
            JOptionPane.showMessageDialog(this, "新增成功", "提示", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "校验失败", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onUpdate() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选中要修改的行", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            Textbook textbook = formPanel.toTextbook(id);
            textbookService.update(textbook);
            reloadTable();
            formPanel.reset();
            JOptionPane.showMessageDialog(this, "修改成功", "提示", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "校验失败", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选中要删除的行", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Long id = (Long) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除该教材吗？", "确认", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            textbookService.delete(id);
            reloadTable();
            formPanel.reset();
        }
    }

    private void reloadTable() {
        List<Textbook> textbooks = textbookService.list();
        tableModel.setData(textbooks);
    }

    private void onTableSelection() {
        int row = table.getSelectedRow();
        if (row >= 0 && row < tableModel.getRowCount()) {
            formPanel.fillFrom(tableModel.getTextbookAt(row));
        }
    }
}
