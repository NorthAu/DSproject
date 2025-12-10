package com.example.textbookmgmt.ui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.function.Consumer;

public class SimpleDocumentAdapter implements DocumentListener {
    private final Consumer<DocumentEvent> handler;

    public SimpleDocumentAdapter(Consumer<DocumentEvent> handler) {
        this.handler = handler;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        handler.accept(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        handler.accept(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        handler.accept(e);
    }
}
