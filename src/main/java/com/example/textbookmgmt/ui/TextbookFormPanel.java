package com.example.textbookmgmt.ui;

import com.example.textbookmgmt.entity.Textbook;

import javax.swing.*;
import java.awt.*;

public class TextbookFormPanel extends JPanel {
    private final JTextField titleField = new JTextField();
    private final JTextField authorField = new JTextField();
    private final JTextField publisherField = new JTextField();
    private final JTextField isbnField = new JTextField();
    private final JSpinner stockSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 10_000, 1));

    public TextbookFormPanel() {
        super(new GridLayout(2, 5, 8, 8));
        setBorder(BorderFactory.createTitledBorder("教材信息"));

        add(new JLabel("书名"));
        add(new JLabel("作者"));
        add(new JLabel("出版社"));
        add(new JLabel("ISBN"));
        add(new JLabel("库存"));

        add(titleField);
        add(authorField);
        add(publisherField);
        add(isbnField);
        add(stockSpinner);
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
