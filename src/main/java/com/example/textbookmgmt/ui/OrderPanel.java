package com.example.textbookmgmt.ui;

import com.example.textbookmgmt.entity.Textbook;
import com.example.textbookmgmt.entity.TextbookOrder;
import com.example.textbookmgmt.service.TextbookOrderService;
import com.example.textbookmgmt.service.TextbookService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderPanel extends JPanel {
    private final TextbookService textbookService;
    private final TextbookOrderService orderService;
    private final JComboBox<Textbook> textbookCombo = new JComboBox<>();
    private final JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
    private final JSpinner arrivedSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
    private final JTextField statusField = new JTextField("已下单", 12);
    private final JTextField orderedDateField = new JTextField(10);
    private final JTextField arrivalDateField = new JTextField(10);
    private final OrderTableModel tableModel = new OrderTableModel();
    private final JTable table = new JTable(tableModel);

    public OrderPanel(TextbookService textbookService, TextbookOrderService orderService) {
        super(new BorderLayout(10, 10));
        this.textbookService = textbookService;
        this.orderService = orderService;
        setBorder(new EmptyBorder(6, 6, 6, 6));

        add(buildForm(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
        reload();
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("教材订购"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("教材:"), gbc);
        gbc.gridx = 1;
        textbookCombo.setPreferredSize(new Dimension(240, 26));
        panel.add(textbookCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("订购数量:"), gbc);
        gbc.gridx = 1;
        panel.add(quantitySpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("到货数量:"), gbc);
        gbc.gridx = 1;
        panel.add(arrivedSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("状态:"), gbc);
        gbc.gridx = 1;
        panel.add(statusField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("下单日期(yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        panel.add(orderedDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("到货日期(yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        panel.add(arrivalDateField, gbc);
        return panel;
    }

    private JScrollPane buildTable() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("订购记录"));
        return scrollPane;
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        JButton save = new JButton("新增订购");
        save.addActionListener(e -> onSave());
        JButton refresh = new JButton("刷新");
        refresh.addActionListener(e -> reload());
        panel.add(save);
        panel.add(refresh);
        return panel;
    }

    private void onSave() {
        try {
            TextbookOrder order = new TextbookOrder();
            Textbook selected = (Textbook) textbookCombo.getSelectedItem();
            order.setTextbookId(selected == null ? null : selected.getId());
            order.setQuantity((Integer) quantitySpinner.getValue());
            order.setArrivedQuantity((Integer) arrivedSpinner.getValue());
            order.setStatus(statusField.getText());
            order.setOrderedDate(parseDate(orderedDateField.getText()));
            order.setArrivalDate(parseDate(arrivalDateField.getText()));
            orderService.create(order);
            reload();
            JOptionPane.showMessageDialog(this, "订购记录已保存", "提示", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "校验失败", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void reload() {
        textbookCombo.removeAllItems();
        for (Textbook textbook : textbookService.list()) {
            textbookCombo.addItem(textbook);
        }
        tableModel.setData(orderService.list());
    }

    private LocalDate parseDate(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        return LocalDate.parse(text.trim());
    }

    static class OrderTableModel extends AbstractTableModel {
        private final String[] columns = {"ID", "教材ID", "数量", "到货", "状态", "下单日期", "到货日期"};
        private List<TextbookOrder> data = new ArrayList<>();

        public void setData(List<TextbookOrder> data) {
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
            TextbookOrder order = data.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> order.getId();
                case 1 -> order.getTextbookId();
                case 2 -> order.getQuantity();
                case 3 -> order.getArrivedQuantity();
                case 4 -> order.getStatus();
                case 5 -> order.getOrderedDate();
                case 6 -> order.getArrivalDate();
                default -> null;
            };
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 0, 1 -> Long.class;
                case 2, 3 -> Integer.class;
                default -> Object.class;
            };
        }
    }
}
