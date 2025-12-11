package com.example.textbookmgmt.ui.model;

import com.example.textbookmgmt.entity.Textbook;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TextbookTableModel extends AbstractTableModel {
    private static final List<String> COLUMNS = List.of("ID", "书名", "作者", "出版社", "ISBN", "教材类型", "库存", "价格(元)");
    private final List<Textbook> data = new ArrayList<>();

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.size();
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS.get(column);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Textbook textbook = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> textbook.getId();
            case 1 -> textbook.getTitle();
            case 2 -> textbook.getAuthor();
            case 3 -> textbook.getPublisher();
            case 4 -> textbook.getIsbn();
            case 5 -> textbook.getTypeName();
            case 6 -> textbook.getStock();
            case 7 -> textbook.getPrice();
            default -> "";
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> Long.class;
            case 6 -> Integer.class;
            case 7 -> java.math.BigDecimal.class;
            default -> String.class;
        };
    }

    public Textbook getTextbookAt(int rowIndex) {
        return data.get(rowIndex);
    }

    public void setData(List<Textbook> textbooks) {
        data.clear();
        data.addAll(textbooks);
        fireTableDataChanged();
    }
}
