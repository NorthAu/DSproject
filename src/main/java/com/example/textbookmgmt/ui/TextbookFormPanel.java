package com.example.textbookmgmt.ui;

import com.example.textbookmgmt.entity.Publisher;
import com.example.textbookmgmt.entity.Textbook;
import com.example.textbookmgmt.entity.TextbookType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TextbookFormPanel extends JPanel {
    private final JTextField titleField = new JTextField();
    private final JTextField authorField = new JTextField();
    private final JTextField publisherField = new JTextField();
    private final JComboBox<Publisher> publisherCombo = new JComboBox<>();
    private final JComboBox<TextbookType> typeCombo = new JComboBox<>();
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
        publisherCombo.setPreferredSize(new Dimension(160, 26));
        typeCombo.setPreferredSize(new Dimension(160, 26));

        addLabeledField("出版社", publisherField, 2, 0);
        addLabeledField("出版社(选择)", publisherCombo, 3, 0);

        addLabeledField("ISBN", isbnField, 0, 1);
        addLabeledField("库存", stockSpinner, 1, 1);
        addLabeledField("教材类型", typeCombo, 2, 1);

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

        Textbook textbook = id == null
                ? new Textbook(title, author, publisher, isbn, stock)
                : new Textbook(id, title, author, publisher, isbn, stock);

        Publisher selectedPublisher = (Publisher) publisherCombo.getSelectedItem();
        TextbookType selectedType = (TextbookType) typeCombo.getSelectedItem();
        if (selectedPublisher != null) {
            textbook.setPublisherId(selectedPublisher.getId());
            if (publisherField.getText().isBlank()) {
                textbook.setPublisher(selectedPublisher.getName());
            }
        }
        if (selectedType != null) {
            textbook.setTypeId(selectedType.getId());
        }
        return textbook;
    }

    public void fillFrom(Textbook textbook) {
        titleField.setText(textbook.getTitle());
        authorField.setText(textbook.getAuthor());
        publisherField.setText(textbook.getPublisher());
        isbnField.setText(textbook.getIsbn());
        stockSpinner.setValue(textbook.getStock());

        selectComboById(publisherCombo, textbook.getPublisherId());
        selectComboById(typeCombo, textbook.getTypeId());
    }

    public void reset() {
        titleField.setText("");
        authorField.setText("");
        publisherField.setText("");
        isbnField.setText("");
        stockSpinner.setValue(1);
        publisherCombo.setSelectedItem(null);
        typeCombo.setSelectedItem(null);
    }

    public void setPublisherOptions(java.util.List<Publisher> publishers) {
        DefaultComboBoxModel<Publisher> model = new DefaultComboBoxModel<>();
        publishers.forEach(model::addElement);
        publisherCombo.setModel(model);
        publisherCombo.setSelectedItem(null);
    }

    public void setTypeOptions(java.util.List<TextbookType> types) {
        DefaultComboBoxModel<TextbookType> model = new DefaultComboBoxModel<>();
        types.forEach(model::addElement);
        typeCombo.setModel(model);
        typeCombo.setSelectedItem(null);
    }

    private <T> void selectComboById(JComboBox<T> comboBox, Long id) {
        if (id == null) {
            comboBox.setSelectedItem(null);
            return;
        }
        ComboBoxModel<T> model = comboBox.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            Object element = model.getElementAt(i);
            if (element instanceof Publisher publisher && id.equals(publisher.getId())) {
                comboBox.setSelectedItem(element);
                return;
            }
            if (element instanceof TextbookType type && id.equals(type.getId())) {
                comboBox.setSelectedItem(element);
                return;
            }
        }
        comboBox.setSelectedItem(null);
    }
}
