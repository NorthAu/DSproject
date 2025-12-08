package com.example.textbookmgmt.ui;

import com.example.textbookmgmt.entity.Textbook;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TextbookFormPanel extends JPanel {
    private final JTextField titleField = new JTextField();
    private final JTextField authorField = new JTextField();
    private final JTextField publisherField = new JTextField();
    private final JTextField isbnField = new JTextField();
    private final JSpinner stockSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 10_000, 1));

    public TextbookFormPanel() {
        super(new GridBagLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("教材信息"),
                new EmptyBorder(8, 8, 8, 8)));

        titleField.setColumns(14);
        authorField.setColumns(12);
        publisherField.setColumns(12);
        isbnField.setColumns(12);

        addLabeledField("书名", titleField, 0, 0);
        addLabeledField("作者", authorField, 1, 0);
        addLabeledField("出版社", publisherField, 2, 0);

        addLabeledField("ISBN", isbnField, 0, 1);
        addLabeledField("库存", stockSpinner, 1, 1);

        JLabel hint = new JLabel("提示：选中表格行后可自动填充表单，并可直接修改保存。");
        hint.setForeground(new Color(70, 70, 70));
        GridBagConstraints hintConstraints = baseConstraints(0, 2);
        hintConstraints.gridwidth = 6;
        hintConstraints.insets = new Insets(10, 0, 0, 0);
        hintConstraints.anchor = GridBagConstraints.WEST;
        add(hint, hintConstraints);
    }

    private void addLabeledField(String label, JComponent field, int gridx, int gridy) {
        int labelColumn = gridx * 2;
        int fieldColumn = labelColumn + 1;
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(jLabel.getFont().deriveFont(Font.BOLD));
        GridBagConstraints labelConstraints = baseConstraints(labelColumn, gridy);
        labelConstraints.weightx = 0;
        labelConstraints.insets = new Insets(2, 2, 6, 2);
        add(jLabel, labelConstraints);

        GridBagConstraints fieldConstraints = baseConstraints(fieldColumn, gridy);
        fieldConstraints.weightx = 1;
        fieldConstraints.insets = new Insets(2, 40, 6, 12);
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        add(field, fieldConstraints);
    }

    private GridBagConstraints baseConstraints(int gridx, int gridy) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(2, 8, 2, 8);
        return constraints;
    }

    public Textbook toTextbook(Long id) {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String publisher = publisherField.getText().trim();
        String isbn = isbnField.getText().trim();
        int stock = (Integer) stockSpinner.getValue();

        return id == null
                ? new Textbook(title, author, publisher, isbn, stock)
                : new Textbook(id, title, author, publisher, isbn, stock);
    }

    public void fillFrom(Textbook textbook) {
        titleField.setText(textbook.getTitle());
        authorField.setText(textbook.getAuthor());
        publisherField.setText(textbook.getPublisher());
        isbnField.setText(textbook.getIsbn());
        stockSpinner.setValue(textbook.getStock());
    }

    public void reset() {
        titleField.setText("");
        authorField.setText("");
        publisherField.setText("");
        isbnField.setText("");
        stockSpinner.setValue(1);
    }
}
