package com.example.textbookmgmt.ui;

import com.example.textbookmgmt.entity.Textbook;
import com.example.textbookmgmt.service.TextbookService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TextbookManagementFrame extends JFrame {
    private final TextbookService textbookService;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField titleField;
    private final JTextField authorField;
    private final JTextField publisherField;
    private final JTextField isbnField;
    private final JSpinner stockSpinner;

    public TextbookManagementFrame(TextbookService textbookService) {
        super("高校教材管理系统");
        this.textbookService = textbookService;
        this.tableModel = new DefaultTableModel(new Object[]{"ID", "书名", "作者", "出版社", "ISBN", "库存"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.table = new JTable(tableModel);
        this.titleField = new JTextField();
        this.authorField = new JTextField();
        this.publisherField = new JTextField();
        this.isbnField = new JTextField();
        this.stockSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 10_000, 1));

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(900, 600));

        add(createFormPanel(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        reloadTable();
    }

    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridLayout(2, 5, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("教材信息"));

        form.add(new JLabel("书名"));
        form.add(new JLabel("作者"));
        form.add(new JLabel("出版社"));
        form.add(new JLabel("ISBN"));
        form.add(new JLabel("库存"));

        form.add(titleField);
        form.add(authorField);
        form.add(publisherField);
        form.add(isbnField);
        form.add(stockSpinner);

        return form;
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
            Textbook textbook = collectFormData(null);
            textbookService.add(textbook);
            reloadTable();
            clearForm();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "校验失败", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onUpdate() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选中要修改的行", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        try {
            Textbook textbook = collectFormData(id);
            textbookService.update(textbook);
            reloadTable();
            clearForm();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "校验失败", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选中要删除的行", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除该教材吗？", "确认", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            textbookService.delete(id);
            reloadTable();
            clearForm();
        }
    }

    private Textbook collectFormData(Long id) {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String publisher = publisherField.getText().trim();
        String isbn = isbnField.getText().trim();
        int stock = (Integer) stockSpinner.getValue();

        if (title.isBlank() || author.isBlank() || publisher.isBlank() || isbn.isBlank()) {
            throw new IllegalArgumentException("书名、作者、出版社、ISBN 均不能为空");
        }

        if (id == null) {
            return new Textbook(title, author, publisher, isbn, stock);
        }
        return new Textbook(id, title, author, publisher, isbn, stock);
    }

    private void reloadTable() {
        List<Textbook> textbooks = textbookService.list();
        tableModel.setRowCount(0);
        for (Textbook textbook : textbooks) {
            tableModel.addRow(new Object[]{
                    textbook.getId(),
                    textbook.getTitle(),
                    textbook.getAuthor(),
                    textbook.getPublisher(),
                    textbook.getIsbn(),
                    textbook.getStock()
            });
        }
    }

    private void clearForm() {
        titleField.setText("");
        authorField.setText("");
        publisherField.setText("");
        isbnField.setText("");
        stockSpinner.setValue(1);
    }
}
